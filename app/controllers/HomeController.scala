package controllers

import collection.JavaConverters._
import javax.inject._

import minesweeper.Minesweeper
import minesweeper.aview.tui.TextUI
import minesweeper.controller.impl.ControllerWrapper
import minesweeper.model.ICell
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val tuiInstance: TextUI = Minesweeper.getGlobalInjector.getInstance(classOf[TextUI])
  val gameController: ControllerWrapper = Minesweeper.getGlobalInjector.getInstance(classOf[ControllerWrapper])

  def index = Action {
    Ok(views.html.index("Welcome to Minesweeper"))
  }

  private def getCells: List[ICell] = {
    val cells: java.util.List[ICell] = gameController.getGrid.getCells

    cells.asScala.toList
  }

  def minesweeper(command: String) = Action {
    tuiInstance.processLine(command)

    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.minesweeperView(gameController.getGrid, tuiAsString))
  }

  def history = Action {
    Ok(views.html.history())
  }
}
