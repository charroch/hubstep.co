package controllers

import play.api.mvc._
import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Crypto

case class Registration(email: String, password: String, confirm: String)

case class Login(email: String, password: String)

object UserController extends Controller {


  def index = Action {
    implicit request =>
      Ok(views.html.register(registrationForm))
  }

  def login = Action {
    implicit request =>
      Ok(views.html.login(loginForm))
  }

  def auth = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        form => BadRequest(views.html.login(form)),
        login => {
          Redirect(routes.Application.index()).flashing("message" -> "User Registered!").withSession("email" -> login.email)
        }
      )
  }

  def register = Action {
    implicit request =>
      registrationForm.bindFromRequest.fold(
        form => BadRequest(views.html.register(form)),
        registration => {
          User.create(User(registration.email, Crypto.sign(registration.password)))
          Redirect(routes.Application.index()).flashing("message" -> "User Registered!")
        }
      )
  }

  def all = Action {
    implicit request =>
      Ok(views.html.users(User.findAll()))
  }

  def registrationForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "confirm" -> nonEmptyText
    )(Registration.apply)(Registration.unapply)
      verifying("Passwords must match", fields => fields match {
      case Registration(_, password, confirmation) => password.equals(confirmation)
    })
      verifying("Email already registered", fields => fields match {
      case Registration(user, _, _) => !User.find(user).isDefined
    })
  )

  def loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
      verifying("Email/Password does not match", fields => fields match {
      case Login(user, password) => {
        User.find(user) match {
          case None => true
          case Some(user) => !(Crypto.sign(user.password) == Crypto.sign(password))
        }
      }
    })
  )
}