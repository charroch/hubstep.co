package plugins

import play.api.Play.current
import play.api.Plugin

trait API extends Plugin {
  val google: String
}

class HeadChef(app: play.api.Application) extends API {
  val google = ""
}

object Application {
  val api: API = current.plugin(classOf[API]).get
}
