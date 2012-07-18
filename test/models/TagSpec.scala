package models

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.FakeApplication
import anorm.NotAssigned

class TagSpec extends Specification {

  "A tag" should {
    "be created if User given" in {
      running(FakeAppNoPlugin) {
        val user = User.create(User("test@novoda.com", "test", null))
        Tag.create("123", user).id should be_!=(NotAssigned)
        Tag.find("123").get.owner should be_==(user)
      }
    }
  }

  val FakeAppNoPlugin = FakeApplication(
    withoutPlugins = Seq("securesocial.core.providers.GoogleProvider"),
    additionalConfiguration = inMemoryDatabase()
  )
}
