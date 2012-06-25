package controllers

import play.api._
import libs.ws.WS
import play.api.mvc._
import securesocial.core.UserService

object Application extends Controller with securesocial.core.SecureSocial {

  def index = Action {
    implicit request =>
      Ok(views.html.index("hello world"))
  }

  def index2 = SecuredAction() {
    implicit request =>
      Ok(views.html.index("Your new application is ready." + request.user))
  }

  def test = SecuredAction() {
    implicit request =>
      Ok(views.html.index("welcome@ " + request.user.displayName))
  }


  def feedTitle(feedUrl: String) = Action {
    Async {
      WS.url(feedUrl).get().map { response =>
        Ok("Feed title: " + (response.json \ "title").as[String])
      }
    }
  }
}