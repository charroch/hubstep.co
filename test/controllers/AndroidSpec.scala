package controllers

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import mocks.MockGoogle
import play.api.mvc.{Result, AsyncResult}


class AndroidSpec extends Specification {

  "An android device" should {
    "create an account if none exist" in {
      running(FakeAppNoPlugin) {
        routeAndCall(
          FakeRequest(GET, "/android/login") withHeaders (("X-Android-Authorization" -> MockGoogle.OK))
        ).get match {
          case AsyncResult(a) => success
          case _ => failure
        }
      }
    }
  }

  val FakeAppNoPlugin = FakeApplication(
    withoutPlugins = Seq("securesocial.core.providers.GoogleProvider", "plugins.HeadChef"),
    additionalConfiguration = inMemoryDatabase(),
    additionalPlugins = Seq("mocks.MockGoogle")
  )
}