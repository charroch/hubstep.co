

package controllers

import play.api.mvc._
import models.User
import play.mvc.Http.Request
import play.api.mvc.Request

/**
 * Created with IntelliJ IDEA.
 * User: acsia
 * Date: 27/06/12
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */

object UserController extends Controller {

  def all = Action {
    implicit request =>
      Ok(views.html.users(User.findAll()))
  }

//  type PartialRequest = Request[AnyContent] => Option[Result]
//
//  val a: PartialRequest = {
//    case Request
//  }

  //  object Request {
  //    def unapply(): Headers =
  //  }

}