package models

import anorm.Pk

case class User (
  id: Pk[Long],
  email: String,
  password: String,
  username: String,
  profiles: Seq[Profile]
)

trait Profile
case class GoogleProfile(id:Long, avatar: String) extends Profile
case class FacebookProfile(id:Long) extends Profile