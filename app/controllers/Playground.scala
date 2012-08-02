package controllers

import play.api.mvc.{Accepting, RequestHeader, Action, Controller}
import security.SecuredAction
import models.{User, UserService}
import play.api.libs.json.{Writes, Json}
import play.api.libs.json.Json._
import com.typesafe.plugin.MailerPlugin

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

  def auth = Action {
    Ok(Json.toJson(Map("token" -> "mytoken", "userid" -> "myuserid")))
  }

  def hooks = Action {
    Ok(Json.toJson(
      Map(
        "hooks" -> Seq(
          Json.toJson(
            Map(
              "name" -> toJson("Email"),
              "id" -> toJson(31),
              "fields" -> toJson("email:String")
            )
          ),
          Json.toJson(
            Map(
              "name" -> toJson("Log"),
              "id" -> toJson(32),
              "fields" -> toJson("None")
            )
          )
        )
      )
    ))
  }

  def createHook = Action {
    Created("hook created")
  }

  def hook(id: String) = Action {
    Ok(Json.toJson(
      Map(
        "name" -> toJson("Log"),
        "id" -> toJson(32),
        "fields" -> toJson("None")
      )
    ))
  }

  def createTag = Action {
    Created("tag created")
  }

  def createMessage(nfc: String) = Action {
    import com.typesafe.plugin._

    val mail = use[MailerPlugin].email
    mail.setSubject("HubStep - pinged")
    mail.addRecipient("carl@novoda.com")
    mail.addFrom("Peter Hausel <carl@hubstep.com>")
    mail.sendHtml("<html>html</html>" )
    Created("message created")
  }
}

class JSON

object JSON {
  def apply(req: RequestHeader) = new JSON
}
