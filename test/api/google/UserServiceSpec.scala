package api.google

import org.specs2.mutable.Specification
import play.api.libs.ws.WS.WSRequestHolder
import org.specs2.mock.Mockito
import play.api.libs.concurrent.Promise
import play.api.libs.ws.Response

class UserServiceSpec extends Specification with Mockito {

  "the Google user service" should {
    "fail if token is invalid" in {
      val m = mock[WSRequestHolder]
      val r = mock[Response]
      r.status returns (200)
      m.get().returns(Promise.pure(r))

      new MockedUserService(m).get(anyString)
    }
  }

  class MockedUserService(r: WSRequestHolder) extends UserService {
    override def request = (s: String) => r
  }
}
