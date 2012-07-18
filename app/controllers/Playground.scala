package controllers

import play.api.mvc.{Accepting, RequestHeader, Action, Controller}
import security.SecuredAction
import models.{User, UserService}
import play.api.libs.json.{Writes, Json}

object Playground extends Controller with SecuredAction {

  def android = Authenticated {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

  def test = Action {
    implicit request =>
     // import models._

      Ok(User.findAll().head) //(User.UserF, User.UserF2)
//
//      request match {
//        case Accepts.Json() => Ok(User.findAll().head)(User.UserF, User.UserF2)
//        case Accepts.Html() => Ok("HTML")
//        case Accepts.Xml() => Ok("XML")
//        case Accepts.JavaScript() => Ok("JavaScript")
//        case _ => Ok("P")
//      }
  }
}

class JSON

object JSON {
  def apply(req: RequestHeader) = new JSON
}
