package test

import org.specs2.mutable.Specification
import com.codahale.jerkson.Json._
import api.Google.GoogleUser
import org.specs2.matcher.Expectable
import play.api.libs.openid.OpenID
import play.api.libs.concurrent.{Thrown, Redeemed}
import java.util.concurrent.TimeUnit

class GoogleSpec extends Specification {

  "A User JSON from Google services" should {
    "parse a full JSON with email" in {
      parse[GoogleUser](
        """{
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
          | }""".stripMargin) should be_==(
        GoogleUser("12345678942613",
          Some("test@gmail.com"),
          Some(true),
          "John Doe",
          "John",
          "Doe",
          "https://plus.google.com/1243",
          "https://lh4.googleusercontent.com/default/",
          "male",
          "en-GB")
      )
    }

    "parse a JSON without email" in {
      parse[GoogleUser](
        """ {
          |"id": "465465465465",
          |"name": "Carl-Gustaf Harroch",
          |"given_name": "Carl-Gustaf",
          |"family_name": "Harroch",
          |"link": "https://plus.google.com/fewfwewfe",
          |"picture": "https://lh6.googleusercontent.com/photo.jpg",
          |"gender": "male",
          |"locale": "en-GB"
          |}""".stripMargin) should be_==(
        GoogleUser("465465465465",
          None,
          None,
          "Carl-Gustaf Harroch",
          "Carl-Gustaf",
          "Harroch",
          "https://plus.google.com/fewfwewfe",
          "https://lh6.googleusercontent.com/photo.jpg",
          "male",
          "en-GB")
      )
    }
  }

  "A call to Google Service" should {
    "fail if wrong token provided" in {
      api.Google.fetch("wrong").extend(_.value match {
        case Thrown(n) => success
        case _ => failure
      }).await(10, TimeUnit.SECONDS).get
    }

    "not fail if token is correct" in {
      api.Google.fetch("ya29.AHES6ZSUBLq7E-AcU6aP8DwsTSI_c_QpLcXam8WaxTlc4g").extend(_.value match {
        case Redeemed(user) => {
          user.name must be_==("Carl-Gustaf Harroch")
          success
        }
        case _ => failure
      }).await(10, TimeUnit.SECONDS).get
    }
  }
}
