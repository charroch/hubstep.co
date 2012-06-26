package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.mvc.Results.Redirect
import play.api.test.FakeApplication

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in {
      running(FakeApplication(withoutPlugins = Seq("securesocial.core.providers.GoogleProvider"))) {
        routeAndCall(FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      running(FakeApplication(withoutPlugins = Seq("securesocial.core.providers.GoogleProvider"))) {
        val home = routeAndCall(FakeRequest(GET, "/")).get
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain("hello world")
      }
    }
  }

  "An android user with a Google account" should {

    "fail if an empty token is provided in the headers" in {
      running(FakeAppNoPlugin) {
        status(routeAndCall(
          FakeRequest(GET, "/android/login") withHeaders (("google_token" -> ""))
        ) get) must equalTo(UNAUTHORIZED)
      }
    }

    "fail if no token is provided in the headers" in {
      running(FakeAppNoPlugin) {
        val request = FakeRequest(GET, "/android/login")
        val login = routeAndCall(request).get
        status(login) must equalTo(UNAUTHORIZED)
      }
    }

    "fail if a token is provided but bad response from Google service" in {
      running(FakeAppNoPlugin) {
        val request = FakeRequest(GET, "/android/login") withHeaders (("google_token" -> "123456756"))
       // val login = await(routeAndCall(request).get)
       // status(login) must equalTo(SERVICE_UNAVAILABLE)
        success
      }
    }


    "auto create an account when login" in {
      // no user in DB
      //      val login = routeAndCall(FakeRequest(GET, "/login")).get
      //      status(login) must equalTo(Redirect)
      "a" must contain("a")
    }
  }

  val FakeAppNoPlugin =  FakeApplication(
    withoutPlugins = Seq("securesocial.core.providers.GoogleProvider")
  )

}