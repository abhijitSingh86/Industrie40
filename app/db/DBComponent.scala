package db

/**
  * Created by billa on 28.12.16.
  */
import slick.driver.JdbcProfile

trait DBComponent {

  val driver: JdbcProfile

  import driver.api._

  val db: Database

}
