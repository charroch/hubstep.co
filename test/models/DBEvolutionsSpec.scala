package models

import org.specs2.mutable.Specification
import play.api.db.DB
import play.api.test._
import play.api.test.Helpers._
import play.api.Play.current
import anorm._


class DBEvolutionsSpec extends Specification {

  "Evolution 1.sql" should {
    "be applied without errors" in {
      evolutionFor("default")
      running(FakeApplication()) {
        DB.withConnection {
          implicit connection =>
            SQL("select count(1) from account").execute()
            SQL("select count(1) from tag").execute()
        }
      }
      success
    }

  }

}
