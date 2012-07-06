package security

import test.HubStepSpecs
import play.api.mvc.BodyParsers.parse
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.{AsyncResult, Results}
import models.{UserService, User}
import mocks.MockGoogle
import play.api.libs.concurrent.{Promise, Thrown, Redeemed}
import java.util.concurrent.{TimeUnit, TimeoutException}
import org.specs2.mock.Mockito

class SecuredActionSpec extends HubStepSpecs with Mockito {

  implicit val us: UserService = smartMock[UserService]

  implicit val ga: Promise[User] = smartMock[Promise[User]]
  ga.orTimeout(any, anyLong, any).returns(Promise.pure(Left(User(anyString))))

  def secure(implicit us: UserService, ga: Promise[User]) = new SecuredAction {
    val userService = us

    override def googleAuth(s: String) = ga
  }

  "A secured action" should {

    "be accessible with user session" in {
      us.find(any[User]) returns Some(User("carl@novoda.com"))
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withSession("email" -> "carl@novoda.com")
        ) should be_==(Results.Ok)
      }
    }

    "be accessible with X-Android-Authentication" in {
      running {
        secure.Authenticated(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withHeaders("X-Android-Authorization" -> MockGoogle.OK)
        ).asInstanceOf[AsyncResult].result.await must beLike {
          case Redeemed(a) => ok
          case _ => ko
        }
      }
    }

    "be inaccessible if X-Android-Authentication is present but Google service unavail" in {
      ga.orTimeout(any, anyLong, any).returns(throw new TimeoutException())
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
