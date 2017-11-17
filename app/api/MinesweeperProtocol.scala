package api

import minesweeper.model.impl.Cell
import minesweeper.model.impl.Grid
import minesweeper.model.ICell
import minesweeper.model.IGrid
import minesweeper.solverplugin.impl.jacop.JacopSolver.SolveResult

import spray.json._

import scala.collection.JavaConverters._

object MinesweeperProtocol extends DefaultJsonProtocol {


  implicit class CellExtension(cell: ICell) {
    def getPosition = Position(cell.getRow, cell.getCol)
  }

  implicit val positionFormat: RootJsonFormat[Position] = jsonFormat2(Position)

  implicit object CellJsonFormat extends RootJsonFormat[ICell] {
    def write(c: ICell): JsObject = {
      JsObject(
        "hasMine" -> JsBoolean(c.isMine),
        "isFlagged" -> JsBoolean(c.isFlag),
        "isRevealed" -> JsBoolean(c.isOpened),
        "position" -> c.getPosition.toJson,
        "surroundingMines" -> JsNumber(c.getMines)
      )
    }

    def read(value: JsValue): ICell = {
      value.asJsObject.getFields("hasMine", "isFlagged", "isRevealed", "position", "surroundingMines") match {
        case Seq(JsBoolean(hasMine),
          JsBoolean(isFlagged),
          JsBoolean(isRevealed),
          position: JsObject,
          JsNumber(surroundingMines)) =>
          val p = position.convertTo[Position]
          val state = if (isRevealed) {
            ICell.State.OPENED
          } else if (isFlagged) {
            ICell.State.FLAG
          } else {
            ICell.State.CLOSED
          }
          new Cell(p.row, p.col, state, surroundingMines.toInt, hasMine)
        case _ => throw DeserializationException("Cell expected")
      }
    }
  }

  implicit object SolveResultJsonFormat extends RootJsonFormat[SolveResult] {
    def write(r: SolveResult): JsObject = {
      JsObject(
        "mineProbabilities" -> JsArray(
          r.cellProb.asScala.map {
          case (cell, probability) =>
            JsObject(
              "cell" -> cell.getPosition.toJson,
              "probability" -> JsNumber(probability)
            )
        }.toVector
        ),
        "clears" -> JsArray(r.clearsToOpen.asScala.map(_.getPosition.toJson).toVector),
        "mines" -> JsArray(r.minesToFlag.asScala.map(_.getPosition.toJson).toVector)
      )
    }

    def read(value: JsValue): SolveResult = {
      throw new UnsupportedOperationException
    }
  }

  implicit object GridJsonFormat extends RootJsonFormat[IGrid[ICell]] {
    def write(g: IGrid[ICell]): JsArray = {
      implicit val p = MinesweeperProtocol

      val cells = g.getNestedCells

      JsArray(cells.asScala
        .map(_.asScala)
        .map(_.map(cell => cell.toJson))
        .map(row => JsArray(row.to[Vector]))
        .to[Vector])
    }

    def read(value: JsValue): IGrid[ICell] = {
      val cells = value match {
        case JsArray(outer_vec: Vector[JsValue]) =>
          outer_vec.map {
            case JsArray(inner_vec: Vector[JsValue]) =>
              inner_vec.map(_.convertTo[ICell])
            case _ => throw DeserializationException("Rows expected")
          }
        case _ => throw DeserializationException("Grid expected")
      }

      val mines = cells.map(_.count(_.isMine)).sum[Int]

      new Grid[ICell](cells.map(_.toArray).toArray, mines)
    }
  }

}
