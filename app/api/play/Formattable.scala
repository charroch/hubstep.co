package api.play

import play.api.mvc.{SimpleResult, Result, RequestHeader}
import play.api.libs.json.Json

trait Formattable {
  //  type ContentTypeWritable[-A] = Acceptable[A] => Writeable[A]

  // Ok
  //def apply[C](content: C)(implicit writeable: Writeable[C], contentTypeOf: ContentTypeOf[C]): SimpleResult[C] = {

}

trait Acceptable {

  def something()(implicit request: RequestHeader) = {

  }

  // OK(Tag)
  // apply[Tag](tag: Tag) => Result
  // Tag => Writable[Tag] && ContentTypeOf[Tag] DEPENDING on JSON/XML etc...
//  def apply[C](content: C)(implicit request: RequestHeader): SimpleResult[C] = {
//    Json.toJson("few").as
//  }
}
