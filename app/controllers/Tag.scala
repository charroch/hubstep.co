package controllers

import play.api.mvc.{Action, Controller}
import security.SecuredAction
import play.api.data.Form
import play.api.data.Forms._
import controllers.Registration
import models.User

case class Tag(tagId: String)

object Tag extends Controller with SecuredAction {

  def push(nfcId: String) = Action {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

  def create = Authenticated {
    implicit request =>
      Ok(views.html.tag_create(createTagForm))
  }

  def createTagForm = Form(
    mapping(
      "tagId" -> nonEmptyText
    )(Tag.apply)(Tag.unapply)
  )

}
