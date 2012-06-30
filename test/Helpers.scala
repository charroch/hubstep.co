package test

import play.api.test.{Helpers, FakeApplication}
import play.api.test.Helpers._

import play.api.mvc._

trait TestUtils {

  def running[T](block: => T): T = {
    Helpers.running(fakeApp)(block)
  }

  def fakeApp = FakeApplication(
    withoutPlugins = Seq("securesocial.core.providers.GoogleProvider", "plugins.HeadChef"),
    additionalConfiguration = inMemoryDatabase(),
    additionalPlugins = Seq("mocks.MockGoogle")
  )
}