package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Promise
import play.api.libs.ws
import play.api.libs.ws.WS
import play.api.Logger

object Android extends Controller {

  def login = Action {
    implicit request =>
      request.headers.get("google_token").map {
        token =>
          if (token.length < 5) {
            Unauthorized("Token must be valid")
          } else {
            Async {
              googleAuth(token).map {
                response =>
                  response.status match {
                    case 200 => Ok("hello world")
                    case _ => Unauthorized("server error " + response.status)
                  }
              }
            }
          }
      } getOrElse (Unauthorized)
  }

  private def google(token: String) = {

  }

  def googleAuth(token: String): Promise[ws.Response] = {
    println(WS.client.getConfig.getMaxRedirects)
    WS.url("http://google.com/").get()
  }
}
