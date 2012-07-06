package mocks

import plugins.API

class MockGoogle(app: play.api.Application) extends API {
//
//  val google = new GoogleAPI {
//    def fetch(token: String): Promise[GoogleUser] = token match {
//      case MockGoogle.OK => Promise.pure(GoogleUser())
//      case MockGoogle.THROW => throw new TimeoutException()
//    }
//
//    def auth[T](token: String)(f: (GoogleUser) => T): Promise[T] = token match {
//      case MockGoogle.OK => Promise.pure(Results.Ok.asInstanceOf[T])
//      case MockGoogle.THROW => throw new TimeoutException()
//    }
//  }
  val google = ""
}

object MockGoogle {
  val OK = "TokenWichGeneratedOk"
  val FORBIDDEN = "nok"
  val TIMEOUT = "timeout"
  val THROW = "throwexception"
}