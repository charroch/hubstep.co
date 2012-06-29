package security

import play.api.mvc._
import play.api.libs
import scala.Some
import models.{UserService, User}
import api.GoogleUser
import play.api.libs.concurrent.{Redeemed, Akka, Promise}
import java.util.concurrent.TimeUnit
import scala.Some


trait SecuredAction {

  val userService: UserService

  case class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) {
      request =>
        request.session.get("email").flatMap(u => userService.find(User(u))).map {
          user =>
            f(AuthenticatedRequest(user, request))
        }.getOrElse(
          request.headers.get("X-Android-Authorization").map(a =>
            AsyncResult.apply(aa(googleAuth(a), request, f))
          ).getOrElse(Results.Unauthorized)
        )
    }
  }

  def aa[A](promiseOfUser: Promise[User], request: Request[A], f: AuthenticatedRequest[A] => Result): Promise[Result] = {
    promiseOfUser.orTimeout(Results.Unauthorized, 10, TimeUnit.SECONDS).map {
      eitherUserOrTimeout =>
        eitherUserOrTimeout match {
          case Left(a) => f(AuthenticatedRequest(a, request))
          case Right(b) => Results.Unauthorized
        }
    }
  }

  def googleAuth(token: String): Promise[User] =
    plugins.Application.api.google.fetch(token).map(
      googleUser =>
        userService.create(googleUser).getOrElse(throw new Exception)
    )


  // Overloaded method to use the default body parser

  import play.api.mvc.BodyParsers._

  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
    Authenticated(parse.anyContent)(f)
  }

  type PartialAction[-A] = (Request[A] => Option[Result])

  val f2: PartialAction[AnyContent] = {
    case a if a.headers.get("A").isDefined => Some(Results.Ok)
  }

  def session[A](f: AuthenticatedRequest[A] => Result): PartialAction[A] = {
    case a if a.session.get("email").isDefined => {
      a.session.get("email").flatMap(u => User.find(u)).map {
        user =>
          f(AuthenticatedRequest(user, a))
      }
    }
  }

  def googleLogin[A](f: AuthenticatedRequest[A] => Result): PartialAction[A] = {
    case a if a.headers.get("X-Android-Authorization").isDefined => {
      a.session.get("X-Android-Authorization").flatMap(u => User.find(u)).map {
        user =>
          f(AuthenticatedRequest(user, a))
      }
    }
  }
}


trait Secured {

  private def check(header: String): Option[String] = {
    val parts = header.split("-")
    if (parts.size != 2) None
    else {
      if (parts(0) == libs.Crypto.sign(parts(1))) Some(parts(1))
      else None
    }
  }

  private def username(request: RequestHeader): Option[String] = {
    request.headers.get("X-Authenticated").map {
      header =>
        check(header)
    }.getOrElse(request.queryString.get("token").map(param => check(param(0))).getOrElse(None))
  }

  private def onUnauthorized(request: RequestHeader) = Results.Unauthorized

  // --
  def isAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) {
    user =>
      Action(request => f(user)(request))
  }
}

