package api

sealed trait MinesweeperAction

object MinesweeperAction {

  case class ChangeSettings(settings: MinesweeperSettings) extends MinesweeperAction

  case class NewGame() extends MinesweeperAction

  case class OpenAround(position: Position) extends MinesweeperAction

  case class OpenCell(position: Position) extends MinesweeperAction

  case class ToggleFlag(position: Position) extends MinesweeperAction

  case class DebugAction(msg: String) extends MinesweeperAction

}
