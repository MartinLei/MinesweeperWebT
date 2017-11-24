package api

import minesweeper.model.ICell
import minesweeper.model.IGrid
import spray.json.DefaultJsonProtocol._
import spray.json._

case class MinesweeperSettings(height: Int, width: Int, mines: Int)

object MinesweeperSettings {
  def fromGrid(grid: IGrid[ICell]): MinesweeperSettings = {
    MinesweeperSettings(grid.getHeight, grid.getWidth, grid.getMines)
  }

  implicit val minesweeperSettingsFormat: RootJsonFormat[MinesweeperSettings] =
    jsonFormat3(MinesweeperSettings.apply)
}
