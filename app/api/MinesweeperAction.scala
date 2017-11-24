package api

import spray.json._

sealed trait MinesweeperAction

object MinesweeperAction {

  case class ChangeSettings(settings: MinesweeperSettings) extends MinesweeperAction

  case class NewGame() extends MinesweeperAction

  case class OpenAround(position: Position) extends MinesweeperAction

  case class OpenCell(position: Position) extends MinesweeperAction

  case class ToggleFlag(position: Position) extends MinesweeperAction

  case class Join() extends MinesweeperAction


  implicit object MinesweeperActionFormat extends RootJsonReader[MinesweeperAction] {
    override def read(json: JsValue): MinesweeperAction = {
      val obj = json.asJsObject
      obj.getFields("action") match {
        case Seq(JsString("changeSettings")) =>
          obj.getFields("settings") match {
            case Seq(settings: JsObject) => ChangeSettings(settings.convertTo[MinesweeperSettings])
            case _ => deserializationError("settings must be a object")
          }
        case Seq(JsString("newGame")) => NewGame()
        case Seq(JsString("openAround")) =>
          obj.getFields("position") match {
            case Seq(position: JsObject) => OpenAround(position.convertTo[Position])
            case _ => deserializationError("incorrect position")
          }
        case Seq(JsString("openCell")) =>
          obj.getFields("position") match {
            case Seq(position: JsObject) => OpenCell(position.convertTo[Position])
            case _ => deserializationError("incorrect position")
          }
        case Seq(JsString("toggleFlag")) =>
          obj.getFields("position") match {
            case Seq(position: JsObject) => ToggleFlag(position.convertTo[Position])
            case _ => deserializationError("incorrect position")
          }
        case Seq(JsString("join")) => Join()
        case _ => deserializationError("action field missing")
      }
    }
  }


}
