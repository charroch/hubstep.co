package security

import play.api.mvc._
import play.api.libs
import scala.Some
import models.User
import controllers.routes


trait SecuredAction {
  self: Controller =>

  case class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) {
      request =>
        request.session.get("user").flatMap(u => User.find(u)).map {
          user =>
            f(AuthenticatedRequest(user, request))
        }.getOrElse(Unauthorized)
    }
  }

  // Overloaded method to use the default body parser

  import play.api.mvc.BodyParsers._

  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
    Authenticated(parse.anyContent)(f)
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

trait AndroidToken {

  //  private def username(request: RequestHeader): Option[String] = {
  //    request.headers.get("X-Android-Authenticated").map {
  //      header =>
  //        check(header)
  //    }.getOrElse(request.queryString.get("token").map(param => check(param(0))).getOrElse(None))
  //  }


  def unlift[T, R](f: T => Option[R]): PartialFunction[T, R] = new PartialFunction[T, R] {
    def apply(x: T): R = f(x).get

    def isDefinedAt(x: T): Boolean = f(x).isDefined

    override def lift: T => Option[R] = f
  }
}


