/**
 *
 * Copyright (C) 2011 Typesafe Inc. <http://www.typesafe.com>
 *
 **/

package com.typesafe.play.mini

import play.api.mvc._
import util.control.Exception._
import org.jboss.netty.handler.codec.http.QueryStringDecoder

/**
 * extractors slightly changed for Play but otherwise taken 
 * from the awesome unfiltered library http://unfiltered.databinder.net/Unfiltered.html
 */
object Path {
  def unapply(req: RequestHeader) = Some(req.path)
  def apply(req: RequestHeader) = req.path
}

/**
 * query string
 */
object QueryString {
  def unapply(req: RequestHeader) = req.uri.split('?') match {
    case Array(path) => None
    case Array(path, query) => Some(query)
  }
  def apply(qs: String, param: String) = {
    val decoder = new QueryStringDecoder("?"+qs)
    Option(decoder.getParameters().get(param))
  }
}


/**
 * path segment
 */
object Seg {
  def unapply(path: String): Option[List[String]] = path.split("/").toList match {
    case "" :: rest => Some(rest) // skip a leading slash
    case all => Some(all)
  }
}

/*
 * method
 */
class Method(method: String) {
  def unapply(req: Request[play.api.mvc.AnyContent]) =
    if (req.method.equalsIgnoreCase(method)) Some(req)
    else None
}


object GET extends Method("GET")
object POST extends Method("POST")
object PUT extends Method("PUT")
object DELETE extends Method("DELETE")
object HEAD extends Method("HEAD")
object CONNECT extends Method("CONNECT")
object OPTIONS extends Method("OPTIONS")
object TRACE extends Method("TRACE")

object & { def unapply[A](a: A) = Some(a, a) }

/**
 * Define a set of extractors allowing to pattern match on the Accept HTTP header of a request
 */
trait AcceptExtractors2 {

  /**
   * Common extractors to check if a request accepts JSON, Html, etc.
   * Example of use:
   * {{{
   * request match {
   *   case Accepts.Json() => Ok(toJson(value))
   *   case _ => Ok(views.html.show(value))
   * }
   * }}}
   */
  object Accepts2 {
    val Get = Accepting2("GET")
    val Post = Accepting2("POST")
    val Head = Accepting2("HEAD")
  }

}

/**
 * Convenient class to generate extractors checking if a given mime type matches the Accept header of a request.
 * Example of use:
 * {{{
 * val AcceptsMp3 = Accepting("audio/mp3")
 * }}}
 * Then:
 * {{{
 * request match {
 *   case AcceptsMp3() => ...
 * }
 * }}}
 */
case class Accepting2(val method: String) {
  def unapply(request: RequestHeader): Boolean = request.method.equalsIgnoreCase(method)
}