package models

import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import anorm.~

case class Hook(url: String, tag: Tag, id: Pk[Long] = NotAssigned)

object Hook {

  val parser = {
    get[Pk[Long]]("id") ~ get[String]("url") ~ Tag.parserNoUser map {
      case pk ~ url ~ tag => Hook(url, tag, pk)
    }
  }

  def create(url: String, tag: Tag): Hook = DB.withConnection {
    implicit connection =>
      val id: Long = SQL(
        """
          INSERT INTO hook(url, tag_id) VALUES ({url}, {tag_id});
        """.stripMargin)
        .on(
        'url -> url,
        'tag_id -> tag.id
      ).executeInsert().get
      return Hook(url, tag, new Id(id))
  }

  def against(tag: Tag): Seq[Hook] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          |SELECT *
          |FROM hook
          |INNER JOIN tag
          |ON tag.id=hook.tag_id
          |WHERE hook.tag_id={tagId};
        """.stripMargin).on('tagId -> tag.id).as(Hook.parser *)
  }
}
