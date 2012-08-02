package controllers

import play.api.mvc.{Action, Controller}
import security.SecuredAction

object Hook extends Controller with SecuredAction {

  def create(tagID: String) = Authenticated {
    implicit request =>
      val tag = models.Tag.find(tagID)
      models.Hook.create("http://google.com", tag.get);
      Ok("Say hello to authenticated request")
  }


}
