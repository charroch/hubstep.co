package controllers

import play.api.mvc.{Action, Controller}
import models.User

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
}
