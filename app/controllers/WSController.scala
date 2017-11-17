package controllers

import javax.inject.Inject

import actors.WSActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import api.MinesweeperAction
import api.MinesweeperAction.DebugAction
import api.MinesweeperEvent
import minesweeper.Minesweeper
import minesweeper.controller.impl.ControllerWrapper
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

class WSController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  val gameController: ControllerWrapper = Minesweeper.getGlobalInjector.getInstance(classOf[ControllerWrapper])


  implicit val minesweeperFlowTransformer: MessageFlowTransformer[MinesweeperAction, MinesweeperEvent] =
    MessageFlowTransformer.stringMessageFlowTransformer
      .map(in => DebugAction(in), out => "debug")

  def ws: WebSocket = WebSocket.accept[MinesweeperAction, MinesweeperEvent] { request =>
    ActorFlow.actorRef { out =>
      WSActor.props(out, gameController)
    }
  }
}








