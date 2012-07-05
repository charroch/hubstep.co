package api

import com.codahale.jerkson.JsonSnakeCase
import play.api.libs.concurrent.Promise
import play.api.libs.ws.WS
import play.api.http.Status
import models.User
import play.api.libs.Crypto
import api.Google.Errors
import play.api.libs.ws.WS.WSRequestHolder
import play.data.validation.Validation
import com.ning.http.client.Realm
















case class Error(domain: String, reason: String, message: String, locationType: String, location: String)

case class GoogleError(errors: List[Error], code: Int, message: String)

case class GoogleAPIError(error: GoogleError)

case class JSONError(error: GoogleError)

object Google {

  case class Error(val statusCode: Int, val statusMessage: String, val json: JSONError)

  sealed abstract class GoogleAPIError(val id: String, val message: String) extends Throwable

  object Errors {

    object MISSING_PARAMETERS extends GoogleAPIError("missing_parameters", """The  server omitted parameters in the callback.""")

    object AUTH_ERROR extends GoogleAPIError("auth_error", """The  server the validation of the user informations.""")

    object BAD_RESPONSE extends GoogleAPIError("bad_response", """Bad response from the server.""")

    object NO_SERVER extends GoogleAPIError("no_server", """The OpenID server could not be resolved.""")

    object NETWORK_ERROR extends GoogleAPIError("network_error", """Couldn't contact the server.""")

    //
    //    503 / Service Unavaiable (slow down)
    //    501 / not implemented
    //      500 / internal server error
    //    404 / not found
    //      403 / forbidden
    //    400 / Bad request
    //      307 / templates redirect
    //      304 / not modified
    //      301 / moved permanently
  }

  val defaultRequest = WS.url("https://www.googleapis.com/oauth2/v1/userinfo");
  val authHeader = (token: String) => ("Authorization" -> ("Bearer %s" format token))

  def apply() = new Google(defaultRequest, authHeader)
}

case class ValidationPromised[E, A](promised: Promise[Either[E, A]]) {
  def map[B](f: A => B): ValidationPromised[E, B] =
    ValidationPromised(promised map {
      valid =>
        valid.fold(
          fail => Left(fail),
          suc => Right(f(suc))
        )
    })

  def flatMap[B](f: A => ValidationPromised[E, B]): ValidationPromised[E, B] =
    ValidationPromised(promised flatMap {
      valid =>
        valid.fold(
          bad => Promise.pure(Left(bad)),
          good => f(good).promised
        )
    })
}



