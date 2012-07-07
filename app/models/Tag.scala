package models

import anorm.{NotAssigned, Pk}

case class Tag(tagID: String, owner: User, id: Pk[Int] = NotAssigned)

object Tag {
}
