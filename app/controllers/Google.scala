package controllers

import play.api.mvc._
import play.api.libs.concurrent.Promise
import play.api.libs.ws
import play.api.libs.ws.WS
import java.util.concurrent.TimeUnit

object Google extends Controller {

  case class GoogleUser(id: Long, email: String)

  case class AuthenticatedRequest[A](val user: GoogleUser, request: Request[A]) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) {
      request =>
      // request.headers.get("from").
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


  /**
   * Fetch a JSON
   *
   * @param token
   * @return
   */
  def googleUserInfo(token: String): Promise[ws.Response] = {
    WS.url("https://www.googleapis.com/oauth2/v1/userinfo").withHeaders("Authorization" -> ("Bearer %s" format token)).get()
  }
}
