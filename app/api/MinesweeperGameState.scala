package api

trait MinesweeperGameState {

}

object MinesweeperGameState {

  case object Running extends MinesweeperGameState

  case object FirstClick extends MinesweeperGameState

  case object Win extends MinesweeperGameState

  case object Lose extends MinesweeperGameState

}
