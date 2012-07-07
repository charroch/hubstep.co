package controllers

import play.api._
import libs.ws.WS
import play.api.mvc._
import securesocial.core.UserService

object Application extends Controller with securesocial.core.SecureSocial {

  def index = Action {
    implicit request =>
      Ok(views.html.landing())
  }

}