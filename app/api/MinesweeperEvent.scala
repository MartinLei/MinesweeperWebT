package api

import minesweeper.model.ICell
import minesweeper.model.IGrid

sealed trait MinesweeperEvent {}

object MinesweeperEvent {

  case class DimensionsChanged(settings: MinesweeperSettings, grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class MultipleCellsChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class NoCellChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class SingleCellChanged(grid: IGrid[ICell], gameState: MinesweeperGameState, position: Position) extends MinesweeperEvent

  case object DebugEvent extends MinesweeperEvent

}

