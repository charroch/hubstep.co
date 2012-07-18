import models.{Hook, Tag, User}
import play.api._
import libs.Crypto
import util.Random

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    app.mode match {
      case Mode.Dev => fixture
      case _ =>
    }
  }

  private def fixture {
    Logger.info("Adding user test@novoda.com with pass test123")
    val user = User.find("test@novoda.com").getOrElse(
      User.create(User("test@novoda.com", Crypto.sign("test123")))
    )

    val tags = 1 to 10 map {
      i =>
        Tag.create(Random.nextString(15), user);
    }

    tags.foreach(
      tag =>
        1 to 5 foreach (i => Hook.create(Random.nextString(15), tag))
    )
  }
}