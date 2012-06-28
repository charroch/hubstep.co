package controllers


import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}

class AuthenticationSpec extends Specification {

  "A registered user" should {
    "be able to login using X-Android-Authorization token" in {
      running(FakeAppNoPlugin) {
        status(routeAndCall(
          FakeRequest(GET, "/android/login") withHeaders (("X-Android-Authorization" -> "123"))
        ) get) must equalTo(UNAUTHORIZED)
      }

    }
  }

  val FakeAppNoPlugin = FakeApplication(
    withoutPlugins = Seq("securesocial.core.providers.GoogleProvider"),
    additionalConfiguration = inMemoryDatabase()
  )
}