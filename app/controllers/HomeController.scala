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
    Ok(views.html.index())
  }

  def minesweeper(command: String) = Action {
    if (command != "") {
      tuiInstance.processLine(command)
    }

    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.game(gameController.getGrid, tuiAsString))
  }

  def history = Action {
    Ok(views.html.history())
  }

  def polymerGame = Action{
    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.polymerGame(gameController.getGrid, tuiAsString))
    //Ok(views.html.polymerGame());
  }
}
