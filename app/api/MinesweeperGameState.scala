package api

import spray.json._


sealed trait MinesweeperGameState

object MinesweeperGameState extends MinesweeperGameState {
  def apply(gameStateName: String): MinesweeperGameState = gameStateName match {
    case "Running" => Running
    case "FirstClick" => FirstClick
    case "Win" => Win
    case "Lose" => Lose
    case other => throw new IllegalArgumentException(other)
  }

  case object Running extends MinesweeperGameState {
    override def toString: String = "Running"
  }

  case object FirstClick extends MinesweeperGameState {
    override def toString: String = "FirstClick"
  }

  case object Win extends MinesweeperGameState {
    override def toString: String = "Win"
  }

  case object Lose extends MinesweeperGameState {
    override def toString: String = "Lose"
  }

  implicit object MinesweeperGameStateWriter extends RootJsonWriter[MinesweeperGameState] {
    override def write(obj: MinesweeperGameState): JsValue = {
      JsString(obj.toString)
    }
  }
}
