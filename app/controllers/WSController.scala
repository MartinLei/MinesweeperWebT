package controllers

import javax.inject.Inject

import actors.WSActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import api.MinesweeperAction
import api.MinesweeperEvent
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import minesweeper.GridFactoryProviders
import minesweeper.MinesweeperModule
import minesweeper.aview.tui.TextUI
import minesweeper.controller.impl.ControllerWrapper
import models.User
import org.apache.log4j.Logger
import play.api.http.websocket.BinaryMessage
import play.api.http.websocket.CloseCodes
import play.api.http.websocket.CloseMessage
import play.api.http.websocket.Message
import play.api.http.websocket.TextMessage
import play.api.libs.streams.ActorFlow
import play.api.libs.streams.AkkaStreams
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import spray.json._
import utils.auth.DefaultEnv

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class WSController @Inject()
(
  cc: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit
  system: ActorSystem,
  mat: Materializer,
  ec: ExecutionContext
) extends AbstractController(cc) {

  val userToController: mutable.Map[User, ControllerWrapper] = mutable.WeakHashMap.empty[User, ControllerWrapper]

  private def createNewIndependentController() = {
    val injector = MinesweeperModule.getInjector(GridFactoryProviders.debugEasy)

    val tuiInstance: TextUI = injector.getInstance(classOf[TextUI])
    tuiInstance.setPrintCommands(false)

    injector.getInstance(classOf[ControllerWrapper])
  }

  def ws: WebSocket = WebSocket.accept[MinesweeperAction, MinesweeperEvent] { request: RequestHeader =>
    implicit val req: Request[AnyContentAsEmpty.type] = Request(request, AnyContentAsEmpty)

    val future: Future[Flow[Any, Nothing, _]] = silhouette.UserAwareRequestHandler { userAwareRequest =>
      Future.successful(HandlerResult(Ok, Some(userAwareRequest.identity)))
    }.map {
      case HandlerResult(r, Some(Some(user))) =>
        val controller = userToController.get(user) match {
          case Some(c) => c
          case None =>
            val controller = createNewIndependentController()
            userToController += (user -> controller)
            controller
        }

        ActorFlow.actorRef { out =>
          WSActor.props(out, controller)
        }
      case HandlerResult(r, _) =>
        // users without login get their own controller/game
        ActorFlow.actorRef { out =>
          WSActor.props(out, createNewIndependentController())
        }
    }
    Await.result(future, 1.second)
  }(new MinesweeperMessageFlowTransformer)

}

class MinesweeperMessageFlowTransformer extends MessageFlowTransformer[MinesweeperAction, MinesweeperEvent] {
  private val stringToMinesweeperAction = { in: String =>
    val jsonAst: JsValue = in.parseJson
    val action: MinesweeperAction = jsonAst.convertTo[MinesweeperAction]
    action
  }

  private val minesweeperEventToString = { out: MinesweeperEvent =>
    val jsonAst: JsValue = out.toJson
    val json = jsonAst.prettyPrint
    json
  }

  private def errorToJsonString(e: Throwable) = {
    s"""{
       |  "error": "${e.getClass.getName}",
       |  "message": "${e.getLocalizedMessage}"
       |}
    """.stripMargin
  }

  override def transform(flow: Flow[MinesweeperAction, MinesweeperEvent, _]): Flow[Message, Message, _] = {
    val resFlow = AkkaStreams.bypassWith[Message, MinesweeperAction, Message](Flow[Message] collect {
      case TextMessage(text) =>
        Try(stringToMinesweeperAction(text)) match {
          case Failure(e) =>
            Right(CloseMessage(CloseCodes.Unacceptable, errorToJsonString(e)))
          case Success(action) =>
            Left(action)
        }
      case BinaryMessage(_) =>
        Right(CloseMessage(CloseCodes.Unacceptable, "This WebSocket only supports text frames"))
    })(flow map { minesweeperEvent =>
      Try(minesweeperEventToString(minesweeperEvent)) match {
        case Failure(e) => CloseMessage(CloseCodes.Unacceptable, errorToJsonString(e))
        case Success(text) => TextMessage(text)
      }
    })
    resFlow.log("WS Flow",
      {
        case message: CloseMessage =>
          Logger.getLogger(this.getClass).info("WS CloseMessage:\n" + message)
        case _ =>
      })
  }
}





