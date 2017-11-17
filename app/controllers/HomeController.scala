package controllers

import javax.inject._

import minesweeper.Minesweeper
import minesweeper.aview.tui.TextUI
import minesweeper.controller.impl.ControllerWrapper
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val tuiInstance: TextUI = Minesweeper.getGlobalInjector.getInstance(classOf[TextUI])
  val gameController: ControllerWrapper = Minesweeper.getGlobalInjector.getInstance(classOf[ControllerWrapper])

  tuiInstance.setPrintCommands(false)

  def index = Action {
    Ok(views.html.index("Welcome to Minesweeper"))
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
