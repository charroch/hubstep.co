package security

import play.api.mvc._
import play.api.libs
import scala.Some


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
}

