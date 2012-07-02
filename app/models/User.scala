package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current
import util.Random

case class User(email: String,
                password: String,
                profile: Option[Profile] = None,
                googleInfo: Option[GoogleInfo] = None,
                id: Pk[Long] = NotAssigned)

case class Profile(firstName: String,
                   lastName: String,
                   givenName: String,
                   picture: String)

case class GoogleInfo(googleId: String,
                      verifiedEmail: Boolean,
                      link: String,
                      picture: String,
                      gender: String,
                      locale: String)

trait UserService {
  def create(user: User): Option[User]

  def find(user: User): Option[User]
}

object UserP {

  val profileParser = {
    get[String]("firstName") ~
      get[String]("lastName") ~
      get[String]("givenName") ~
      get[String]("picture") map {
      case firstName ~ lastName ~ givenName ~ picture =>
        Profile(firstName, lastName, givenName, picture)
    }
  }

  val googleInfo = {
    get[String]("googleId") ~
      get[Boolean]("verifiedEmail") ~
      get[String]("link") ~
      get[String]("picture") ~
      get[String]("gender") ~
      get[String]("locale") map {
      case googleId ~ verifiedEmail ~ link ~ picture ~ gender ~ locale =>
        GoogleInfo(googleInfo, verifiedEmail, link, picture, gender, locale)
    }

  }

  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("email") ~
      get[String]("password") ~
      profileParser ~ googleInfo map {
      case pk ~ email ~ password ~ pp ~ gi =>
        User(email, password, Some(pp), Some(gi), pk)
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
        return User(user.email)
    }
  }

  def apply(email: String) = User(email, Random.nextString(64))
}