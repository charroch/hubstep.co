package controllers

import play.api.mvc._
import security.SecuredAction
import play.api.data.Form
import play.api.data.Forms._
import models.User
import models.{Tag => Tags, Hook => Hooks}
import play.api.http.{ContentTypeOf, Writeable}
import play.api.Logger
import scala.Some

case class Tag(tagId: String)

object Tag extends Controller with SecuredAction {

  implicit val tagWritable = new Writeable[Tags](tag =>
    "HElloWorld".getBytes
  )

  implicit val contentType = new ContentTypeOf[Tags](mimeType = Some("text/html"))


  //  type ContentTypeWritable[-A] = Acceptable[A] => Writeable[A]

  // Ok
  //def apply[C](content: C)(implicit writeable: Writeable[C], contentTypeOf: ContentTypeOf[C]): SimpleResult[C] = {

  /**
   * List all tags created by a user.
   *
   * @return
   */
  def all = Authenticated {
    implicit request =>
      Ok(views.html.all_tags(
        Tags belongsTo request.user
      )) //(Writable, contenttype)
  }

  def view(nfcId: String) = Authenticated {
    implicit request =>
      (for {
        tag <- (Tags find nfcId)
        hooks = (Hooks against tag)
      } yield (Ok(views.html.tag.single(tag, hooks)))).getOrElse(NotFound("Tag not found for id " + nfcId))
  }


  class Method(method: String) {
    def unapply(request: RequestHeader) = if (request.method.equalsIgnoreCase(method)) {
      Some(request)
    } else {
      None
    }
  }

  case object GET extends Method("GET")

  case object POST extends Method("POST")

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
        case POST(r) => {
          createTagForm.bindFromRequest.fold(
            form => BadRequest(views.html.tag_create(form)),
            tag => {
              models.Tag.create(tag.tagId, request.user)
              Redirect(routes.Application.index()).flashing("message" -> "Tag Registered!")
            }
          )
        }
        case GET(r) => Ok(views.html.tag_create(createTagForm))
        //case Accepts.Html => Ok(views.html.tag_create(createTagForm))
        //case Accepts.Json => Redirect(routes.Application.index())
        //case _ => Ok(views.html.tag_create(createTagForm))
      }
  }

  private def insertTag(user: User, tag: Tag) {
    models.Tag.create(tag.tagId, user)
  }

  def createTagForm = Form(
    mapping(
      "tagId" -> nonEmptyText
    )(Tag.apply)(Tag.unapply) verifying("Tag already registered", fields => fields match {
      case Tag(id) => models.Tag.find(id).isEmpty
    })
  )
}
