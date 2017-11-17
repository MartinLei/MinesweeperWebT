package api

import spray.json._
import spray.json.DefaultJsonProtocol._

case class Position(row: Int, col: Int) {

}

object Position {
  implicit val positionFormat: RootJsonFormat[Position] = jsonFormat2(Position.apply)
}

