package security

import test.{fakeAppw, HubStepSpecs}
import play.api.mvc.BodyParsers.parse
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.{AsyncResult, Results}
import models.User
import mocks.MockGoogle
import play.api.libs.concurrent.{Promise, Thrown, Redeemed}
import java.util.concurrent.TimeoutException
import org.specs2.mock.Mockito
import api.google.{API, Profile}
import org.specs2.matcher.Matcher
import org.specs2.mutable.Around
import org.specs2.execute.Result

class SecuredActionSpec extends HubStepSpecs with Mockito {

  class MockedSecuredAction extends SecuredAction {
    override val userRepository = mock[UserRepository].smart
    override val userService = mock[MockedUserService].smart

    /**
     * To please mocking the trait method, we need our own class...
     */
    class MockedUserService extends UserService {
      override def get(r: String): Promise[Either[API.Error, Profile]] = super.get(r)
    }

  }

  val secure = new MockedSecuredAction

  implicit def matchUser(user: User): Matcher[User] = ((_: User).email == (user.email),
    (_: User).email + " was not the same as " + user.email
    )

  "A secured action" should {

    "be accessible with user session" in {
      secure.userRepository.find(any[User]).returns(Some(User("carl@novoda.com")))
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withSession("email" -> "carl@novoda.com")
        ) should be_==(Results.Ok) and (there was one(secure.userRepository).find(
          argThat(matchUser(User("carl@novoda.com")))
        ))
      }
    }

    "should fail if user session exist but no user in DB" in {
      secure.userRepository.find(any[User]).returns(None)
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withSession("email" -> "carl@novoda.com")
        ) should be_==(Results.Unauthorized)
      }
    }

    "be accessible with X-Android-Authentication" in {
      val fee = mock[Promise[Either[API.Error, Profile]]].smart
      fee.map(anyFunction1[Either[API.Error, Profile], User]).returns(Promise.pure(User("carl@novoda.com")))
      secure.userService.get("tokentousertest").returns(fee)

      running {
        secure.Authenticated(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withHeaders("X-Android-Authorization" -> "tokentousertest")
        ).asInstanceOf[AsyncResult].result.await must beLike {
          case Redeemed(a) => ok
          case _ => ko
        }
      }
    }

    "create a user with X-Android-Authentication if no user in DB" in {
      running {
        userRepository.find(any[User]) returns None
        secure.Authenticated(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withHeaders("X-Android-Authorization" -> MockGoogle.OK)
        ).asInstanceOf[AsyncResult].result.await must beLike {
          case Redeemed(a) => there was one(userRepository).create(any[User])
          case _ => ko
        }
      }
    }.pendingUntilFixed

    "be inaccessible if X-Android-Authentication is present but Google service unavail" in {
      //ga.orTimeout(any, anyLong, any).returns(Promise.pure(any[Exception])))
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything").withHeaders("X-Android-Authorization" -> MockGoogle.THROW)
        ).asInstanceOf[AsyncResult].result.await must beLike {
          case Thrown(a) => ok
          case a: TimeoutException => ok
          case _ => ko("exception thrown")
        }
      }
    }.pendingUntilFixed

    "be inaccessible if none of the above is defined" in {
      running {
        secure.Authenticated(parse.anyContent)(authRequest => Results.Ok)(
          FakeRequest(GET, "/anything")
        ) should be_==(Results.Unauthorized)
      }
    }
  }
}
