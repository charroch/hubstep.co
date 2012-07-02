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

  "An android user with a Google account" should {

    "fail if an empty token is provided in the headers" in {
      running(FakeAppNoPlugin) {
        status(routeAndCall(
          FakeRequest(GET, "/android/login") withHeaders (("google_token" -> ""))
        ) get) must equalTo(UNAUTHORIZED)
      }
    }.pendingUntilFixed

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
    }.pendingUntilFixed


    "auto create an account when login" in {
      failure
    }.pendingUntilFixed
  }

  val FakeAppNoPlugin = FakeApplication(
    withoutPlugins = Seq("securesocial.core.providers.GoogleProvider"),
    additionalConfiguration = inMemoryDatabase()
  )

}