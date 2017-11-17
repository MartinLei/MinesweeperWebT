package api

import spray.json._
import spray.json.DefaultJsonProtocol._

case class MinesweeperSettings(height: Int, width: Int, mines: Int)

object MinesweeperSettings {
  implicit val minesweeperSettingsFormat: RootJsonFormat[MinesweeperSettings] =
    jsonFormat3(MinesweeperSettings.apply)
}
