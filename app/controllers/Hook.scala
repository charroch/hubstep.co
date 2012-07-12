package controllers

import play.api.mvc.{Action, Controller}
import security.SecuredAction

object Hook extends Controller with SecuredAction {

  def create(tagID: String) = Authenticated {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

}
