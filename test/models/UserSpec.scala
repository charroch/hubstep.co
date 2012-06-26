package models

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class UserSpec extends Specification {

  "A user" should {
    "be created" in {
      running(FakeApplication()) {
        User.create(User("carl@novoda.com", "test", null))
        User.findAll() must have size 1
      }
    }
  }
}
