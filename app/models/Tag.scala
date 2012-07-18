package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import anorm.~
import scala.Some

case class Tag(tagID: String, owner: User, id: Pk[Long] = NotAssigned)

object Tag {

  val parserNoUser = {
    get[Pk[Long]]("id") ~ get[String]("tagId") map {
      case pk ~ tagId => Tag(tagId, null, pk)
    }
  }

  val parser = {
    get[Pk[Long]]("id") ~ get[String]("tagId") ~ User.parser map {
      case pk ~ tagId ~ user => Tag(tagId, user, pk)
    }
  }

  def create(tagId: String, user: User): Tag = DB.withConnection {
    implicit connection =>
      val id: Long = SQL(
        """
          INSERT INTO tag(tagId, account_id) VALUES ({tagId}, {account_id});
        """.stripMargin)
        .on(
        'tagId -> tagId,
        'account_id -> user.id
      ).executeInsert().get
      return Tag(tagId, user, new Id(id))
  }

  def find(tagId: String): Option[Tag] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          |SELECT *
          |FROM tag
          |INNER JOIN account
          |ON account.id=tag.account_id
          |WHERE tagId={tagId};
        """.stripMargin).on('tagId -> tagId).as(Tag.parser *) match {
        case Nil => None
        case head :: Nil => Some(head)
      }
  }

  def belongsTo(user: User): Seq[Tag] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          |SELECT *
          |FROM tag
          |INNER JOIN account
          |ON account.id=tag.account_id
          |WHERE tag.account_id={userId};
        """.stripMargin).on('userId -> user.id).as(Tag.parser *)
  }

}
