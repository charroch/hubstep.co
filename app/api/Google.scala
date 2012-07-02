package api

import com.codahale.jerkson.JsonSnakeCase
import play.api.libs.concurrent.Promise
import play.api.libs.ws.WS
import play.api.http.Status
import play.api.mvc.Result
import models.User
import play.api.libs.Crypto


/**
 * A user as given by the API calls against Google's user profile
 *
 * @param id
 * @param email
 * @param verifiedEmail
 * @param name
 * @param givenName
 * @param familyName
 * @param link
 * @param picture
 * @param gender
 * @param locale
 */
@JsonSnakeCase
case class GoogleUser(id: String,
                      email: Option[String],
                      verifiedEmail: Option[Boolean],
                      name: String,
                      givenName: String,
                      familyName: String,
                      link: String,
                      picture: String,
                      gender: String,
                      locale: String)

object GoogleUser {

  /**
   * Transforming a GoogleUser into a User insertable into the DB
   *
   * @param gu
   * @return
   */
  implicit def toUser(gu: GoogleUser): User = {
    new User(gu.email.getOrElse("noemail"), Crypto.sign(gu.id))
  }

  def apply() = new GoogleUser("1234", Some("c@a"), Some(true), "john", "doe", "doe", "", "", "", "")
}

trait GoogleAPI {

  def fetch(token: String): Promise[GoogleUser]

  def auth[T](token: String)(f: GoogleUser => T): Promise[T]
}

class Google extends GoogleAPI {

  /**
   *
   * @param token the AUTH token as given by Google Android's account manager
   * @return a Google User if successful
   */
  def fetch(token: String): Promise[GoogleUser] = {
    import com.codahale.jerkson.Json._
    WS.url("https://www.googleapis.com/oauth2/v1/userinfo").withHeaders("Authorization" -> ("Bearer %s" format token))
      .get().map(response => {
      response.status match {
        case Status.OK => parse[GoogleUser](response.body)
        case _ => throw Errors.BAD_RESPONSE
      }
    })
  }

  def auth[T](token: String)(f: (GoogleUser) => T) = fetch(token).map(f)
}

object Google {
  def apply() = new Google
}

sealed abstract class GoogleAPIError(val id: String, val message: String) extends Throwable

object Errors {

  object MISSING_PARAMETERS extends GoogleAPIError("missing_parameters", """The  server omitted parameters in the callback.""")

  object AUTH_ERROR extends GoogleAPIError("auth_error", """The  server the validation of the user informations.""")

  object BAD_RESPONSE extends GoogleAPIError("bad_response", """Bad response from the server.""")

  object NO_SERVER extends GoogleAPIError("no_server", """The OpenID server could not be resolved.""")

  object NETWORK_ERROR extends GoogleAPIError("network_error", """Couldn't contact the server.""")

}


