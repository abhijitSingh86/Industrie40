package db

/**
  * Created by billa on 28.12.16.
  */
import slick.driver.MySQLDriver

trait MySqlDBComponent extends DBComponent {

  val driver = MySQLDriver

  import driver.api._

  val db: Database = MySqlDB.connectionPool

}

private[db] object MySqlDB {

  import slick.driver.MySQLDriver.api._

  val connectionPool = Database.forConfig("dbs")

}