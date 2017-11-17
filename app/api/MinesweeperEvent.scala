package api

import minesweeper.model.ICell
import minesweeper.model.IGrid
import spray.json._

sealed trait MinesweeperEvent {}

object MinesweeperEvent {

  case class DimensionsChanged(settings: MinesweeperSettings, grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class MultipleCellsChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class NoCellChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class SingleCellChanged(grid: IGrid[ICell], gameState: MinesweeperGameState, position: Position) extends MinesweeperEvent

  case object DebugEvent extends MinesweeperEvent

  implicit object MinesweeperEventFormat extends RootJsonWriter[MinesweeperEvent] {
    override def write(obj: MinesweeperEvent): JsValue = ???
  }

}

