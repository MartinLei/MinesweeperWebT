package controllers

import javax.inject._

import minesweeper.Minesweeper
import minesweeper.aview.tui.TextUI
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val tuiInstance: TextUI = Minesweeper.getTuiInstance

  def index = Action {
    Ok(views.html.index("Welcome to Minesweeper"))
  }

  def tui(command: String) = Action {
    tuiInstance.processLine(command)
    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.tui(tuiAsString))
  }

  def history = Action {
    Ok(views.html.history())
  }
}
