package plugins

import play.api.Play.current
import play.api.Plugin
import api.GoogleAPI

trait API extends Plugin {
  val google: GoogleAPI
}

class HeadChef(app: play.api.Application) extends API {
  val google = api.Google
}

object Application {
  val api: API = current.plugin(classOf[API]).get
}
