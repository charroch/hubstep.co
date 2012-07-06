package security

import test.HubStepSpecs
import play.api.mvc.BodyParsers.parse
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.{AsyncResult, Results}
import models.{UserService, User}
import mocks.MockGoogle
import play.api.libs.concurrent.{Thrown, Redeemed}
import java.util.concurrent.TimeoutException

class SecuredActionSpec extends HubStepSpecs {

  val secure = new SecuredAction {
    val userService = new UserService {
      def create(user: User): Option[User] = Some(User("test@novoda.com"))
      def find(user: User) = Some(User("test@novoda.com"))
    }
  }

  "A secured action" should {

    "be accessible with user session" in {
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withSession("email" -> "carl@novoda.com")
        ) should be_==(Results.Ok)
      }
    }

    "be accessible with X-Android-Authentication" in {
      running {
        val result2 = secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withHeaders("X-Android-Authorization" -> MockGoogle.OK)
        )
        result2.asInstanceOf[AsyncResult].result.await must beLike {
          case Redeemed(a) => ok
          case _ => ko
        }
      }
    }

    "be inaccessible if X-Android-Authentication is present but Google service unavail" in {
      running {
        val result2 = secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withHeaders("X-Android-Authorization" -> MockGoogle.THROW)
        )
        result2.asInstanceOf[AsyncResult].result.await must beLike {
          case Thrown(a) => ok
          case a: TimeoutException => ok
          case _ => ko("exception thrown")
        }
      }
    }

    "be inaccessible if none of the above is defined" in {
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything")
        ) should be_==(Results.Unauthorized)
      }
    }
  }
}
