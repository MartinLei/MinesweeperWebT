package controllers

import javax.inject.Inject

import actors.WSActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import api.MinesweeperAction
import api.MinesweeperEvent
import minesweeper.Minesweeper
import minesweeper.controller.impl.ControllerWrapper
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import spray.json._

class WSController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  val gameController: ControllerWrapper = Minesweeper.getGlobalInjector.getInstance(classOf[ControllerWrapper])

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

  implicit val minesweeperFlowTransformer: MessageFlowTransformer[MinesweeperAction, MinesweeperEvent] =
    MessageFlowTransformer.stringMessageFlowTransformer.map(stringToMinesweeperAction, minesweeperEventToString)

  def ws: WebSocket = WebSocket.accept[MinesweeperAction, MinesweeperEvent] { request =>
    ActorFlow.actorRef { out =>
      WSActor.props(out, gameController)
    }
  }
}








