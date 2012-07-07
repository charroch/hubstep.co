package controllers

import play.api.mvc.{Action, Controller}

object Tag extends Controller {

  def push(nfcId: String) = Action {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

}
