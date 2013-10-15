
import play.api.libs.concurrent.Promise
import play.api.libs.ws.{Response => WSResponse, WS}
import models.User
import play.api.libs.Crypto
import play.api.http.Status
import scala.Right
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.json.{JsObject, Reads, JsValue}
import api.google.API.Error.ErrorRead
import api.google.API.Error
import scala.concurrent.ExecutionContext.Implicits.global

trait API[T, P] {

  def setup: P => WSRequestHolder

  def parser: Reads[T]

  def get(r: P): Promise[Either[Error, T]] = setup(r).get().map(
    response =>
      response.status match {
        case Status.OK => Right(parser.reads(response.json))
        case _ => Left(Error(response.json))
      }
  )
}

case class Profile(id: String, email: String, verifiedEmail: Boolean, name: String, givenName: String, familyName: String, link: String, picture: String, gender: String, locale: String)

object Profile {
  def apply(response: WSResponse): Profile = ProfileRead.reads(response.json)

  def apply(json: JsValue): Profile = ProfileRead.reads(json)

  implicit def toUser(p: Profile): User = new User(p.email, Crypto.sign(p.id))

  object ProfileRead extends Reads[Profile] {
    def reads(json: JsValue): Profile = Profile(
      (json \ "id").as[String],
      (json \ "email").as[String],
      (json \ "verified_email").as[Boolean],
      (json \ "name").as[String],
      (json \ "given_name").as[String],
      (json \ "family_name").as[String],
      (json \ "link").as[String],
      (json \ "picture").as[String],
      (json \ "gender").as[String],
      (json \ "locale").as[String]
    )
  }

}

object API {

  case class Error(errors: Seq[ErrorMessage], code: Int, message: String)

  case class ErrorMessage(domain: String, reason: String, message: String, locationType: String, location: String)

  object Error {

    def apply(response: WSResponse): Error = ErrorRead.reads(response.json)

    def apply(json: JsValue): Error = ErrorRead.reads(json)

    implicit object ErrorRead extends Reads[Error] {

      def reads(json: JsValue): Error = Error(
        toErrorMessage((json \ "error")),
        (json \ "error" \ "code").as[Int],
        (json \ "error" \ "message").as[String])

      private def toErrorMessage(json: JsValue): Seq[ErrorMessage] = {
        (json \ "errors").as[List[JsObject]].map(error =>
          ErrorMessage(
            (error \ "domain").as[String],
            (error \ "reason").as[String],
            (error \ "message").as[String],
            (error \ "locationType").as[String],
            (error \ "location").as[String]
          )
        )
      }
    }

  }

}

/**
 * Main landing trait
 */
trait UserServiceComponent {

  val userService: UserService = new UserService

  class UserService extends API[Profile, String] {
    def setup = (token: String) => WS.url("https://www.googleapis.com/oauth2/v1/userinfo").withHeaders(("Authorization" -> ("Bearer %s" format token)))
    def parser = Profile.ProfileRead
  }

}