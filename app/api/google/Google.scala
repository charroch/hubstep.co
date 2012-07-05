package api.google

import com.codahale.jerkson.JsonSnakeCase


trait UserInfo {

}

object UserInfo {
  @JsonSnakeCase
  case class GoogleUser(id: String,                        email: Option[String],                        verifiedEmail: Option[Boolean],                        name: String,                        givenName: String,                        familyName: String,                        link: String,                       picture: String,
                        gender: String,
                        locale: String)

  case class Error(domain: String, reason: String, message: String, locationType: String, location: String)

}

class Google(val request: WSRequestHolder, val authHeader: String => (String, String)) extends GoogleAPI {

  def fetch(token: String): Promise[Either[GoogleAPIError, GoogleUser]] = {
    import com.codahale.jerkson.Json._
    request.withHeaders(authHeader(token)).get().map(response => {
      response.status match {
        case Status.OK => Right(parse[GoogleUser](response.body))
        case _ => Left(parse[GoogleAPIError](response.body))
      }
    })
  }

  /**
   *
   * @param token the AUTH token as given by Google Android's account manager
   * @return a Google User if successful
   */
  def fetch(token: String): Promise[GoogleUser] = {
    import com.codahale.jerkson.Json._
    request.withHeaders(authHeader(token)).get().map(response => {
      response.status match {
        case Status.OK => parse[GoogleUser](response.body)
        //case Status.SERVICE_UNAVAILABLE => response.status
        case _ => throw Errors.BAD_RESPONSE
      }
    })
  }

  def auth[T](token: String)(f: (GoogleUser) => T) = fetch(token).map(f)
}

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

case class Error(domain: String, reason: String, message: String, locationType: String, location: String)

object Error {
  def apply(domain: String, reason: String, message: String, locationType: String, location: String): Error = throw new Error()

  def unapply(x$0: Error): scala.Option[(String, String, String, String, String)] = throw new Error()
}

case class GoogleError(errors: List[Error], code: Int, message: String)

object GoogleError {
  def apply(errors: List[Error], code: Int, message: String): GoogleError = throw new Error()

  def unapply(x$0: GoogleError): scala.Option[(List[Error], Int, String)] = throw new Error()
}

case class GoogleAPIError(error: GoogleError)

object GoogleAPIError {
  def apply(error: GoogleError): GoogleAPIError = throw new Error()

  def unapply(x$0: GoogleAPIError): scala.Option[GoogleError] = throw new Error()
}

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