package actors

import akka.actor._
import api.MinesweeperAction
import api.MinesweeperAction.ChangeSettings
import api.MinesweeperAction.Join
import api.MinesweeperAction.NewGame
import api.MinesweeperAction.OpenAround
import api.MinesweeperAction.OpenCell
import api.MinesweeperAction.ToggleFlag
import api.MinesweeperEvent
import minesweeper.controller.IMinesweeperControllerSolvable

object WSActor {
  def props(out: ActorRef, gameController: IMinesweeperControllerSolvable) = Props(new WSActor(out, gameController))
}

class WSActor(out: ActorRef, gameController: IMinesweeperControllerSolvable) extends Actor {
  println("WSActor created")

  def receive: PartialFunction[Any, Unit] = {
    case action: MinesweeperAction =>
      println("WSActor: " + action)
      action match {
        case ChangeSettings(settings) =>
          gameController.changeSettings(settings.height, settings.width, settings.mines)
        case NewGame() =>
          gameController.newGame()
        case OpenAround(position) =>
          gameController.openAround(position.row, position.col)
        case OpenCell(position) =>
          gameController.openCell(position.row, position.col)
        case ToggleFlag(position) =>
          gameController.toggleFlag(position.row, position.col)
        case Join() =>
      }

      val minesweeperEvent = MinesweeperEvent(
        gameController.getEvent,
        gameController.getGrid,
        gameController.getGameStateString
      )

      send(minesweeperEvent)
    case unknownMessage => println("WSActor: unknown message: " + unknownMessage)
  }

  def send(event: MinesweeperEvent): Unit = {
    out ! event
  }

  override def postStop(): Unit = {
    println("WSActor: stopped")
  }
}