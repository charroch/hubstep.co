package controllers

import play.api.mvc.{Action, Controller}
import security.SecuredAction
import models.{User, UserService}

object Playground extends Controller with SecuredAction {

  def android = Authenticated {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

  val userService = new UserService {
    def create(user: User): Option[User] = Some(User("test@novoda.com"))
    def find(user: User) = Some(User("test@novoda.com"))
  }

}
