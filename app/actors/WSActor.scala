package actors

import akka.actor._
import api.MinesweeperAction.DebugAction
import api.MinesweeperEvent.DebugEvent
import minesweeper.controller.IMinesweeperControllerSolvable

object WSActor {
  def props(out: ActorRef, gameController: IMinesweeperControllerSolvable) = Props(new WSActor(out, gameController))
}

class WSActor(out: ActorRef, gameController: IMinesweeperControllerSolvable) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case msg: DebugAction =>
      println("WS: " + msg)
      out ! (DebugEvent)
  }
}