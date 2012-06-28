package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Promise
import models.User
import java.util.concurrent.TimeUnit

object Android extends Controller {

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
              plugins.Application.api.google.auth(token) {
                googleUser =>
                  import api.GoogleUser._
                  User.create(googleUser)
                  Ok(views.html.index("Your new application is ready." + googleUser.email.getOrElse("no email :(")))
              }.orTimeout(Unauthorized, 10, TimeUnit.SECONDS).flatMap(
                _ match {
                  case Left(a) => Promise.pure(a)
                  case Right(n) => Promise.pure(n)
                }
              )
            }
          }
      } getOrElse (Unauthorized)
  }
}


