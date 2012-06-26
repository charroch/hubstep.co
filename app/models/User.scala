package models

import anorm.{NotAssigned, Pk}
import anorm.SqlParser._
import java.util
import anorm._
import play.api.db.DB
import play.api.Play.current

case class User(email: String, password: String, fullname: String, isAdmin: Boolean = false, id: Pk[Int] = NotAssigned)
case class Tag(title: String, posted: util.Date, content: String, author: User)

object User {

  val parser = {
    get[Pk[Int]]("id") ~
      get[String]("email") ~
      get[String]("password") ~
      get[Option[String]]("fullname") ~
      get[Boolean]("isAdmin") map {
      case pk ~ mail ~ name ~ fullname ~ isAdmin => User(mail, name, fullname.getOrElse(null), isAdmin, pk)
    }
  }

  def findAll(): Seq[User] = DB.withConnection {
    implicit connection =>
      SQL("select * from account").as(User.parser *)
  }


  def find(user: User): Option[User] = None

  def create(user: User): User = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into account(email, password, username, fullname, isadmin) values ({email}, {password}, {username}, {fullname}, {isAdmin});").on(
          'email -> user.email,
          'password -> user.password,
          'username -> user.email,
          'fullname -> user.fullname,
          'isAdmin -> user.isAdmin
        ).executeUpdate()
        val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
          case Row(id: Int) => id
        }.head
        return User(user.email, user.password, user.fullname, user.isAdmin, new Id(id))
    }
  }

}