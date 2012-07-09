package controllers

import play.api.mvc.{RequestHeader, Action, Controller}
import security.SecuredAction
import play.api.data.Form
import play.api.data.Forms._

case class Tag(tagId: String)

object Tag extends Controller with SecuredAction {

  case class Accepting2(val method: String) {
    def unapply(request: RequestHeader): Boolean = request.method.equalsIgnoreCase(method)
  }

  def push(nfcId: String) = Action {
    implicit request =>
      Ok("Say hello to authenticated request")
  }

  def create = Authenticated {
    implicit request =>

      request match {
        case Accepting2("GET") => Ok("Hello world")
        case Accepting2("POST") => Ok("world")
        case Accepts.Html => Ok(views.html.tag_create(createTagForm))
        case Accepts.Json => Redirect(routes.Application.index())
        case _ => Ok(views.html.tag_create(createTagForm))
      }
  }

  def createTagForm = Form(
    mapping(
      "tagId" -> nonEmptyText
    )(Tag.apply)(Tag.unapply)
  )
}
