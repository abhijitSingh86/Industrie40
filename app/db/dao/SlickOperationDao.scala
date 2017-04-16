package db.dao

import controllers.{ApiError, ApiErrors, ApiResponse}
import db.DBComponent
import dbgeneratedtable.Tables
import models.Operation

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by billa on 27.12.16.
  */
trait SlickOperationDao{
  this: DBComponent =>

  import driver.api._

  private val operations = Tables.Operation

  def selectByOperationId(operationId:Int):models.Operation ={
      Await.result(db.run(operations.filter(_.id === operationId).result.headOption),Duration.Inf ) match{
        case Some(x:Tables.OperationRow) =>new Operation(x.id,x.name)

      }
  }

  def selectAllOperations():List[models.Operation] ={
    Await.result(db.run(operations.result),Duration.Inf ) match{
      case x:IndexedSeq[Tables.OperationRow] =>x.map(y=> new Operation(y.id,y.name)).toList
    }
  }

  def deleteOperation(id:Int):Boolean = {
    Await.result(db.run(operations.filter(_.id === id).delete),Duration.Inf) match {
      case i:Int => true
      case _ => false
    }
  }

  def add(operation:Operation ):ApiResponse[Int] = {
    val o = db.run(operations returning operations.map(_.id) += Tables.OperationRow(0,operation.name))
    ApiResponse.Async.Right(o)
  }
}
