package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current
import scala.util.Random
import play.api.libs.json.{Json, JsValue, Format}
import play.api.http.{ContentTypeOf, Writeable}
import play.api.mvc.RequestHeader

case class User(email: String, password: String, id: Pk[Long] = NotAssigned)

trait UserRepositoryComponent {
  val userRepository = new UserRepository

  class UserRepository {
    def create(user: User): Option[User] = Some(User.create(user))

    def find(user: User): Option[User] = User.find(user)
  }

}

trait UserService {
  def create(user: User): Option[User]

  def find(user: User): Option[User]
}

object User {

  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("email") ~
      get[String]("password") map {
      case pk ~ email ~ password => User(email, password, pk)
    }
  }

  def findAll(): Seq[User] = DB.withConnection {
    implicit connection =>
      SQL("select * from account").as(User.parser *)
  }


  def find(user: User): Option[User] = DB.withConnection {
    implicit connection =>
      SQL("select * from account where email={email};").on('email -> user.email).as(User.parser *) match {
        case Nil => None
        case head :: Nil => Some(head)
      }
  }

  def find(email: String): Option[User] = find(User(email))

  def create(user: User): User = {
    DB.withConnection {
      implicit connection =>
        val id: Long = SQL(
          """
          INSERT INTO account(email, password) VALUES ({email}, {password});
          """.stripMargin)
          .on(
          'email -> user.email,
          'password -> user.password
        ).executeInsert().get
        return User(user.email, user.password, new Id(id))
    }
  }

  def apply(email: String) = new User(email, Random.alphanumeric.take(6).mkString)

  implicit object UserFormat extends Format[User] {
    def reads(json: JsValue) = null

    def writes(user: User) = Json.toJson(4)
  }


  implicit def PersonShow(implicit request: RequestHeader): Writeable[User] = {
    if (request.accepts("application/json")) {
      new Writeable[User](a => "JSON".getBytes)
    } else if (request.accepts("application/xml")) {
      new Writeable[User](a => "XML".getBytes)
    } else {
      new Writeable[User](a => "Hello World".getBytes)
    }
  }


  //extends Writeable[User](a => "Hello World".getBytes)

  implicit object UserF2 extends ContentTypeOf[User](mimeType = Some("application/json"))

}
