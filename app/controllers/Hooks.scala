package controllers

import play.api.mvc.{Action, Controller}

object Hooks extends Controller {

  def create(tagID: String) = Action {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

}
