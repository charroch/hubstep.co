package models

import anorm.{NotAssigned, Pk}
import anorm.SqlParser._
import java.util
import anorm._
import play.api.db.DB
import play.api.Play.current


//sealed trait User
//
//case class SimpleUser(email: String, password: String, id: Pk[Long] = NotAssigned) extends User
//
//case class LoggedInUser(id: Pk[Long]) extends SimpleUser

case class User(email: String, password: String, username: String, fullname: String, isAdmin: Boolean = false, id: Pk[Long] = NotAssigned)

case class Tag(title: String, posted: util.Date, content: String, author: User)

object User {

  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("email") ~
      get[String]("password") ~
      get[String]("username") ~
      get[Option[String]]("fullname") ~
      get[Boolean]("isAdmin") map {
      case pk ~ mail ~ password ~ username ~ fullname ~ isAdmin => User(mail, password, username, fullname.getOrElse(null), isAdmin, pk)
    }
  }

  def findAll(): Seq[User] = DB.withConnection {
    implicit connection =>
      SQL("select * from account").as(User.parser *)
  }


  def find(user: User): Option[User] = None

  def find(email: String): Option[User] = None

  def create(user: User): User = {
    DB.withConnection {
      implicit connection =>
        val id: Long = SQL(
          """
          INSERT INTO account(email, password, username, fullname, isadmin) VALUES ({email}, {password}, {username}, {fullname}, {isAdmin});
          """.stripMargin)
          .on(
          'email -> user.email,
          'password -> user.password,
          'username -> user.email,
          'fullname -> user.fullname,
          'isAdmin -> user.isAdmin
        ).executeInsert().get
        return User(user.email, user.password, user.username, user.fullname, user.isAdmin, new Id(id))
    }
  }

}