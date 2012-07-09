package test

import play.api.test.{Helpers, FakeApplication}
import play.api.test.Helpers._

import play.api.mvc._
import org.specs2.mutable.{Around, Specification}
import api.google.{API, Profile, UserServiceComponent}
import models.{User, UserRepositoryComponent}
import org.specs2.mock.Mockito
import play.api.libs.concurrent.Promise
import org.specs2.execute

trait HubStepSpecs extends TestingEnvironment {

  def running[T](block: => T): T = {
    Helpers.running(fakeApp)(block)
  }

  def fakeApp = FakeApplication(
    withoutPlugins = Seq("plugins.HeadChef"),
    additionalConfiguration = inMemoryDatabase()
  )


}

trait TestingEnvironment extends UserServiceComponent with UserRepositoryComponent with Specification with Mockito {

  override val userRepository = mock[UserRepository].smart
  override val userService = mock[MockedUserService].smart

  /**
   * To please mocking the trait method, we need our own class...
   */
  class MockedUserService extends UserService {
    override def get(r: String): Promise[Either[API.Error, Profile]] = super.get(r)
  }

}

object fakeAppw extends Around {

  def around[T](t: => T)(implicit evidence$1: (T) => execute.Result) = Helpers.running(fakeApp)(t)

  def fakeApp = FakeApplication(
    withoutPlugins = Seq("plugins.HeadChef"),
    additionalConfiguration = inMemoryDatabase()
  )
}