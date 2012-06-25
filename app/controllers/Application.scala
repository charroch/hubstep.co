package controllers

import play.api.mvc._
import play.api.libs.concurrent.Promise
import play.api.libs.ws.WS
import play.api.libs.ws

object Application extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction() {
    implicit request =>
      Ok(views.html.index("Your new application is ready." + request.user))
  }

  def test = SecuredAction() {
    implicit request =>
      Ok(views.html.index("welcome@ " + request.user.displayName))
  }


  val twitter: Promise[ws.Response] = WS.url("http://search.twitter.com/search.json?q=blue%20angels&rpp=5&include_entities=true&result_type=mixed").get()

  val promiseOfResult: Promise[Result] = twitter.map { pi =>
    Ok("PI value computed: " + pi)
  }

  def async = Action {
    Async {
      twitter.map {
        response =>
          Ok("Feed title: " + (response.json \ "results"))
      }
    }
  }

}