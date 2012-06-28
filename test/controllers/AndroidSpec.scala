package controllers

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import plugins.API
import api.GoogleAPI
import org.specs2.mock.Mockito


class AndroidSpec extends Specification with Mockito {

  class MockGoogle extends API {
    val google = mock[GoogleAPI]
  }

  "An android device" should {
    "create an account if none exist" in {
        running(FakeAppNoPlugin) {
            status(routeAndCall(
                FakeRequest(GET, "/android/login") withHeaders (("X-Android-Authorization" -> ""))
            ) get) must equalTo(UNAUTHORIZED)
        }
    }
  }

  val FakeAppNoPlugin = FakeApplication(
      withoutPlugins = Seq("securesocial.core.providers.GoogleProvider"),
      additionalConfiguration = inMemoryDatabase(),
      additionalPlugins = Seq("controllers.AndroidSpec#MockGoogle")
  )
}