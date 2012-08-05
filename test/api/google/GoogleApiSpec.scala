package api.google

import org.specs2.mutable.Specification
import play.api.libs.ws.WS.WSRequestHolder
import org.specs2.mock.Mockito
import play.api.libs.concurrent.{Redeemed, Promise}
import play.api.libs.ws.Response
import play.api.libs.json.{JsValue, Reads, Json}
import api.google.API.{ErrorMessage, Error}
import play.api.libs.concurrent._
import play.api.libs.concurrent.execution.defaultContext

class GoogleApiSpec extends Specification with Mockito {

  val jsonProfile = Json.parse( """{
                                  |  "id": "12345678942613",
                                  |  "email": "test@gmail.com",
                                  |  "verified_email": true,
                                  |  "name": "John Doe",
                                  |  "given_name": "John",
                                  |  "family_name": "Doe",
                                  |  "link": "https://plus.google.com/1243",
                                  |  "picture": "https://lh4.googleusercontent.com/default/",
                                  |  "gender": "male",
                                  |  "locale": "en-GB"
                                  | }""".stripMargin)

  val profile = Profile("12345678942613", "test@gmail.com", true, "John Doe", "John", "Doe", "https://plus.google.com/1243", "https://lh4.googleusercontent.com/default/", "male", "en-GB")

  val jsonError = Json.parse( """
                                |{
                                | "error": {
                                |  "errors": [
                                |   {
                                |    "domain": "global",
                                |    "reason": "required",
                                |    "message": "Required",
                                |    "locationType": "parameter",
                                |    "location": "resource.longUrl"
                                |   }
                                |  ],
                                |  "code": 400,
                                |  "message": "Required"
                                | }
                                |}
                              """.stripMargin)

  "A User JSON from Google services" should {
    "parse a full JSON with email" in {
      Profile(jsonProfile) should be_==(profile)
    }

    "parse a failed response as JSON" in {
      Error(jsonError) should be_==(
        Error(List(
          ErrorMessage("global", "required", "Required", "parameter", "resource.longUrl")
        ), 400, "Required")
      )
    }
  }

  "the Google user service" should {

    val m = mock[WSRequestHolder]
    val r = mock[Response]

    m.get().returns(Promise.pure(r))

    "give back profile if 200 returned by server" in {
      r.status returns (200)
      new MockedApiService(m).get("any").await match {
        case Redeemed(r) if r.isRight => ok
        case _ => ko
      }
    }

    "fail if not 200" in {
      r.status returns (300)
      r.json returns jsonError
      new MockedApiService(m).get("any").await match {
        case Redeemed(r) if r.isLeft => ok
        case _ => ko
      }
    }
  }

  class MockedApiService(r: WSRequestHolder) extends API[Profile, Any] {
    def setup = (a: Any) => r

    def parser = new Reads[Profile] {
      def reads(json: JsValue) = profile
    }
  }
}
