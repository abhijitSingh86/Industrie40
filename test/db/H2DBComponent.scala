package db
import java.util.UUID

import org.slf4j.LoggerFactory
import slick.driver.{JdbcProfile, MySQLDriver}

/**
  * Created by billa on 29.12.16.
  */
trait H2DBComponent extends DBComponent{

  val logger = LoggerFactory.getLogger(this.getClass)

//  override val driver: JdbcProfile = slick.driver.MySQLDriver//slick.driver.H2Driver

  val driver = MySQLDriver

  import driver.api._


  val randomDB = "jdbc:h2:mem:test" + UUID.randomUUID().toString() + ";"

//  val h2Url = randomDB + "MODE=MySql;DATABASE_TO_UPPER=false;INIT=runscript from '/home/billa/workspace/schedulingapplication/target/scala-2.11/test-classes/resources/schema.sql'\\;" //runscript from 'src/test/resources/schemadata.sql'

  override val db: Database = MySqlTestDb.connectionPool
}


private[db] object MySqlTestDb {

  import slick.driver.MySQLDriver.api._

  val connectionPool = {

    val dbMySqlUrl = "jdbc:mysql://address=(host=localhost)(user=root)(password=toor)(protocol=tcp)/test"
    Database.forURL(url = dbMySqlUrl, driver = "com.mysql.jdbc.Driver")//"org.h2.Driver")
  }
}