package api

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.concurrent.{Thrown, Redeemed, Promise}
import play.api.libs.ws.Response

class GoogleUserProfileSpec extends Specification with Mockito {

  val okJSON = """{
  "id": "12345678942613",
  "email": "test@gmail.com",
  "verified_email": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "link": "https://plus.google.com/1243",
  "picture": "https://lh4.googleusercontent.com/default/",
  "gender": "male",
  "locale": "en-GB"
 }"""

  val request = smartMock[WSRequestHolder]
  val response = smartMock[Response]

  request.withHeaders(any).returns(request)
  request.get().returns(Promise.pure(response))

  "The Google user profile API" should {

    "be accessible given correct token and correct response code" in {
      response.status returns (200)
      response.body returns okJSON
      val gu = new Google(request, Google.authHeader).fetch("sometoken")
      gu.await match {
        case Thrown(e) => ko("should have been redeemed, but it throwed %s" format e)
        case Redeemed(r) => ok
      }
    }

    "be a failure if response code not 200" in {
      response.status returns (400)
      response.body returns okJSON
      new Google(request, Google.authHeader).fetch("sometoken").await match {
        case Thrown(e) => ok
        case Redeemed(r) => ko("should not have been redeemed %s" format r)
      }
    }
  }

}
