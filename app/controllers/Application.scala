package controllers

import play.api._
import play.api.mvc._
import securesocial.core.UserService

object Application extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction() {
    implicit request =>
      Ok(views.html.index("Your new application is ready." + request.user))
  }

  def test = SecuredAction() {
    implicit request =>
      Ok(views.html.index("welcome@ " + request.user.displayName))
  }

}