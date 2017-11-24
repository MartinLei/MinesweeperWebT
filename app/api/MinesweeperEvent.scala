package api

import minesweeper.controller.events.{DimensionsChanged => ControllerDimensionsChanged}
import minesweeper.controller.events.{MultipleCellsChanged => ControllerMultipleCellsChanged}
import minesweeper.controller.events.{NoCellChanged => ControllerNoCellChanged}
import minesweeper.controller.events.{SingleCellChanged => ControllerSingleCellChanged}
import minesweeper.model.ICell
import minesweeper.model.IGrid
import minesweeper.util.observer.Event
import spray.json._
sealed trait MinesweeperEvent {
  val grid: IGrid[ICell]
  val gameState: MinesweeperGameState
}

object MinesweeperEvent {
  def apply(event: Event, grid: IGrid[ICell], gameStateName: String): MinesweeperEvent = {
    val gameState = MinesweeperGameState(gameStateName)

    Option(event) match {
      case Some(_: ControllerDimensionsChanged) => DimensionsChanged(grid, gameState)
      case Some(_: ControllerMultipleCellsChanged) => MultipleCellsChanged(grid,gameState)
      case Some(_: ControllerNoCellChanged) => NoCellChanged(grid,gameState)
      case Some(event: ControllerSingleCellChanged) => SingleCellChanged(grid,gameState, Position(event.getRow, event.getCol))
      case Some(_) | None => NoCellChanged(grid,gameState)
    }
  }

  case class DimensionsChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class MultipleCellsChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class NoCellChanged(grid: IGrid[ICell], gameState: MinesweeperGameState) extends MinesweeperEvent

  case class SingleCellChanged(grid: IGrid[ICell], gameState: MinesweeperGameState, position: Position) extends MinesweeperEvent

  implicit object MinesweeperEventWriter extends RootJsonWriter[MinesweeperEvent] {
    import api.MinesweeperProtocol.GridJsonFormat

    override def write(obj: MinesweeperEvent): JsValue = obj match {
      case DimensionsChanged(grid, gameState) =>
        JsObject(
          "event" -> JsString("DimensionsChanged"),
          "grid" -> grid.toJson,
          "settings" -> MinesweeperSettings.fromGrid(grid).toJson,
          "gameState" -> gameState.toJson
        )
      case MultipleCellsChanged(grid, gameState) =>
        JsObject(
          "event" -> JsString("MultipleCellsChanged"),
          "grid" -> grid.toJson,
          "settings" -> MinesweeperSettings.fromGrid(grid).toJson,
          "gameState" -> gameState.toJson
        )
      case NoCellChanged(grid, gameState) =>
        JsObject(
          "event" -> JsString("NoCellChanged"),
          "grid" -> grid.toJson,
          "settings" -> MinesweeperSettings.fromGrid(grid).toJson,
          "gameState" -> gameState.toJson
        )
      case SingleCellChanged(grid, gameState, position) =>
        JsObject(
          "event" -> JsString("SingleCellChanged"),
          "position" -> position.toJson,
          "grid" -> grid.toJson,
          "settings" -> MinesweeperSettings.fromGrid(grid).toJson,
          "gameState" -> gameState.toJson
        )
    }
  }

}

