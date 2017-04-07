package models

import scala.collection.mutable

/**
  * Created by billa on 2016-12-15.
  */
case class Assembly(id:Int,name:String,totalOperations:List[(Operation,Int)] =List[(Operation,Int)](),
                    allocatedOperations:mutable.ArrayBuffer[Operation] =mutable.ArrayBuffer[Operation]()) {

  def freeOperation(maybeOperation: Option[Operation]):Unit ={
    maybeOperation match{
      case Some(x) => {
        allocatedOperations -= x
      }
      case None =>
    }
  }

  def allocateOperation(operation: Operation): Boolean ={
    synchronized {
      totalOperations.filter(_._1.equals(operation)).contains(operation) match {
        case true =>
          allocatedOperations += operation
          true
        case false => false
      }
    }

  }
}
