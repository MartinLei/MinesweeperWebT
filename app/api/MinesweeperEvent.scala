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

  implicit object MinesweeperEventWriter extends RootJsonWriter[MinesweeperEvent] {
    import api.MinesweeperProtocol.GridJsonFormat

    override def write(obj: MinesweeperEvent): JsValue = obj match {
      case DimensionsChanged(settings, grid, gameState) =>
        JsObject(
          "event" -> JsString("DimensionsChanged"),
          "settings" -> settings.toJson,
          "grid" -> grid.toJson,
          "gameState" -> gameState.toJson
        )
      case MultipleCellsChanged(grid, gameState) =>
        JsObject(
          "event" -> JsString("MultipleCellsChanged"),
          "grid" -> grid.toJson,
          "gameState" -> gameState.toJson
        )
      case NoCellChanged(grid, gameState) =>
        JsObject(
          "event" -> JsString("NoCellChanged"),
          "grid" -> grid.toJson,
          "gameState" -> gameState.toJson
        )
      case SingleCellChanged(grid, gameState, position) =>
        JsObject(
          "event" -> JsString("SingleCellChanged"),
          "position" -> position.toJson,
          "grid" -> grid.toJson,
          "gameState" -> gameState.toJson
        )
    }
  }

}

