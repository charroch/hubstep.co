package controllers

import play.api.mvc.{Action, Controller}

object User extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction() {
    implicit request =>
      Ok("Hello world")
  }
}