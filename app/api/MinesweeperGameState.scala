package api

import enumeratum._
import spray.json._

import scala.collection.immutable


sealed trait MinesweeperGameState extends EnumEntry {

}

object MinesweeperGameState extends Enum[MinesweeperGameState] {

  case object Running extends MinesweeperGameState

  case object FirstClick extends MinesweeperGameState

  case object Win extends MinesweeperGameState

  case object Lose extends MinesweeperGameState

  override def values: immutable.IndexedSeq[MinesweeperGameState] = findValues

  implicit object MinesweeperGameStateWriter extends RootJsonWriter[MinesweeperGameState] {
    override def write(obj: MinesweeperGameState): JsValue = {
      JsString(obj.entryName)
    }
  }
}
