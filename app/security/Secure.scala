package security

import play.api.mvc._
import models.{UserService => DBUserService, UserRepositoryComponent, User}
import play.api.libs.concurrent.{PlayPromise, Promise}
import java.util.concurrent.TimeUnit
import api.google.Profile
import scala.Right
import scala.Left

import play.api.libs.concurrent._
import play.api.libs.concurrent.execution.defaultContext

trait SecuredAction extends api.google.UserServiceComponent with UserRepositoryComponent {

  case class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest(request)

  sealed trait AuthRequest

  case object Session extends AuthRequest {
    def unapply[A](request: play.api.mvc.Request[A]): Option[User] = request.session.get("email").flatMap(
      email => userRepository.find(User(email))
    )
  }

  case object AndroidHeader extends AuthRequest {
    def unapply[A](request: play.api.mvc.Request[A]): Option[Promise[User]] = request.headers.get("X-Android-Authorization").map(
      token => googleAuth(token)
    )
  }

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) {
      request =>
        request match {
          case Session(user) => f(AuthenticatedRequest(user, request))
          case AndroidHeader(user) => AsyncResult.apply(liftToResult(user, request, f))
          case _ => Results.Unauthorized
        }
    }
  }

  import play.api.mvc.BodyParsers._

  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
    Authenticated(parse.anyContent)(f)
  }

  def liftToResult[A](promiseOfUser: Promise[User], request: Request[A], f: AuthenticatedRequest[A] => Result): Promise[Result] = {
    promiseOfUser.orTimeout(Results.Unauthorized, 10, TimeUnit.SECONDS).map {
      eitherUserOrTimeout =>
        eitherUserOrTimeout match {
          case Left(a) => f(AuthenticatedRequest(a, request))
          case Right(b) => Results.Unauthorized
        }
    }
  }

  def googleAuth(token: String)(implicit toUser: Profile => User): Promise[User] = userService.get(token).map(
    googleUser =>
      googleUser.fold(
        throw new Exception, p => userRepository.create(toUser(p)).get
      )
  )

}

