package api.google

import com.codahale.jerkson.JsonSnakeCase
import play.api.libs.concurrent.Promise
import com.codahale.jerkson.Json._
import play.api.libs.ws.{Response => WSResponse, WS}
import models.User
import play.api.libs.Crypto
import play.api.http.Status
import scala.Right
import play.api.libs.ws.WS.WSRequestHolder
import play.api.mvc.Action

trait API[T, P] {

  object Error {
    case class JSONError(error: Error)
    case class Error(errors: List[ErrorMessage], code: Int, message: String)
    case class ErrorMessage(domain: String, reason: String, message: String, locationType: String, location: String)

    import com.codahale.jerkson.Json._

    def apply(response: WSResponse): JSONError = parse[JSONError](response.body)
  }

  type Response = Either[Error.JSONError, T]
  type Request = P => WSRequestHolder

  def request: Request

  def get(r: P): Promise[Response]
}

@JsonSnakeCase
case class Profile(id: String, email: String, verifiedEmail: Boolean, name: String, givenName: String, familyName: String, link: String, picture: String, gender: String, locale: String) {
  implicit def toUser(p: Profile): User = new User(p.email, Crypto.sign(p.id))
}

class UserService extends API[Profile, String] {

  def request = (token: String) => WS.url("https://www.googleapis.com/oauth2/v1/userinfo").withHeaders(("Authorization" -> ("Bearer %s" format token)))

  def get(token: String) = request(token).get().map(
    response =>
      response.status match {
        case Status.OK => Right(parse[Profile](response.body))
        case _ => Left(parse[Error.JSONError](response.body))
      }
  )
}