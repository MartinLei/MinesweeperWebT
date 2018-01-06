package controllers

import javax.inject._

import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import minesweeper.Minesweeper
import minesweeper.aview.tui.TextUI
import minesweeper.controller.impl.ControllerWrapper
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()
(
  cc: ControllerComponents,
  socialProviderRegistry: SocialProviderRegistry
)(
  implicit
  assets: AssetsFinder,
  ex: ExecutionContext
) extends AbstractController(cc) with I18nSupport {
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

  def signIn = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.signIn(socialProviderRegistry))
  }

  def polymerGame = Action{
    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.polymerGame(gameController.getGrid, tuiAsString))
    //Ok(views.html.polymerGame());
  }

  def vueGame = Action{
    Ok(views.html.vueGame());
  }
}
