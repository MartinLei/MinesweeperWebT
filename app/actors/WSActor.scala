package actors

import akka.actor._
import api.MinesweeperAction
import api.MinesweeperEvent
import api.MinesweeperEvent.DebugEvent
import minesweeper.controller.IMinesweeperControllerSolvable

object WSActor {
  def props(out: ActorRef, gameController: IMinesweeperControllerSolvable) = Props(new WSActor(out, gameController))
}

class WSActor(out: ActorRef, gameController: IMinesweeperControllerSolvable) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case action: MinesweeperAction =>
      println("WSActor: " + action)
      send(DebugEvent)
    case unknown => println("WSActor: unknown message: " + unknown)
  }

  def send(event: MinesweeperEvent): Unit = {
    out ! event
  }
}