package controllers

import play.api.mvc.{RequestHeader, Action, Controller}
import play.api.libs.concurrent.Promise
import play.api.libs.ws
import play.api.libs.ws.WS
import play.api.Logger
import api.Google
import models.User

object Android extends Controller with Google {

  object Header {
    val AUTH = "X-Android-Authorization"
    val FROM = "from"
  }

  def login = Action {
    implicit request =>
      request.headers.get(Header.AUTH).map {
        token =>
          if (token.length < 5) {
            Unauthorized("Token must be valid")
          } else {
            Async {
              auth(token) {
                googleUser =>
                  import api.GoogleUser._
                  User.create(googleUser)
                  Ok(views.html.index("Your new application is ready." + googleUser.email.getOrElse("no email :(")))
              }
            }
          }
      } getOrElse (Unauthorized)
  }
}


