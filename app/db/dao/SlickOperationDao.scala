package db.dao

import dbgeneratedtable.Tables
import models.ComponentOperation
import slick.driver.MySQLDriver.profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 27.12.16.
  */
class SlickOperationDao(db:Database) {

  private val operations = Tables.Operation

  def selectByOperationId(operationId:Int):models.Operation ={
      Await.result(db.run(operations.filter(_.id === operationId).result),Duration.Inf ) match{
        case x:Tables.OperationRow =>new ComponentOperation(x.id,x.name)
      }
  }
}
