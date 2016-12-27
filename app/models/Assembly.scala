package models

import scala.collection.mutable

/**
  * Created by billa on 2016-12-15.
  */
case class Assembly(name:String,totalOperations:List[Operation] =List[Operation](),
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
      totalOperations.contains(operation) match {
        case true =>
          allocatedOperations += operation
          true
        case false => false
      }
    }

  }
}
