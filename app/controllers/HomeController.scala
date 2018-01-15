package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import minesweeper.Minesweeper
import minesweeper.aview.tui.TextUI
import minesweeper.controller.impl.ControllerWrapper
import play.api.i18n.I18nSupport
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class HomeController @Inject()
(
  cc: ControllerComponents,
  socialProviderRegistry: SocialProviderRegistry,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit
  assets: AssetsFinder,
  ex: ExecutionContext
) extends AbstractController(cc) with I18nSupport {
  val tuiInstance: TextUI = Minesweeper.getGlobalInjector.getInstance(classOf[TextUI])
  val gameController: ControllerWrapper = Minesweeper.getGlobalInjector.getInstance(classOf[ControllerWrapper])

  tuiInstance.setPrintCommands(false)

  val errorHandler = new SecuredErrorHandler {
    override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
      Future.successful(Redirect("/signIn"))
    }
    override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
      Future.successful(Redirect("/signIn"))
    }
  }

  def index = Action {
    Ok(views.html.index())
  }

  def minesweeper(command: String) = silhouette.SecuredAction(errorHandler) { implicit request =>
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

  def polymerGame = silhouette.SecuredAction(errorHandler) { implicit request =>
    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.polymerGame(gameController.getGrid, tuiAsString))
  }

  def vueGame = silhouette.SecuredAction(errorHandler) { implicit request =>
    Ok(views.html.vueGame())
  }
}
