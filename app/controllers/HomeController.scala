package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.LogoutEvent
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

  def index = silhouette.UserAwareAction { implicit request =>
    Ok(views.html.index(request.identity))
  }

  def minesweeper(command: String) = silhouette.SecuredAction(errorHandler) { implicit request =>
    if (command != "") {
      tuiInstance.processLine(command)
    }

    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.game(tuiAsString, Some(request.identity)))
  }

  def history = silhouette.UserAwareAction { implicit request =>
    Ok(views.html.history(request.identity))
  }

  def signIn = silhouette.UserAwareAction { implicit request =>
      Ok(views.html.signIn(socialProviderRegistry, request.identity))
  }

  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.HomeController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  def polymerGame = silhouette.SecuredAction(errorHandler) { implicit request =>
    val tuiAsString: String = tuiInstance.getTUIAsString
    Ok(views.html.polymerGame(tuiAsString, Some(request.identity)))
  }

  def vueGame = silhouette.SecuredAction(errorHandler) { implicit request =>
    Ok(views.html.vueGame(Some(request.identity)))
  }
}
