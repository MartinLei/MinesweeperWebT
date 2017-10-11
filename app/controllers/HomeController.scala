package controllers

import javax.inject._

import minesweeper.Minesweeper
import minesweeper.aview.tui.TextUI
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val tui: TextUI = Minesweeper.getTuiInstance

  def index = Action {
    Ok(views.html.index("Welcome to Minesweeper"))
  }

  def minesweeper(command: String) = Action {
    tui.processLine(command)
    val tuiAsString: String = tui.getTUIAsString
    Ok(views.html.minesweeper(tuiAsString))
  }
}
