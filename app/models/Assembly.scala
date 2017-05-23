package models

import scala.collection.mutable

/**
  * Created by billa on 2016-12-15.
  */

sealed trait AssemblyOperationStatus{
  def text:String
}

case object BusyOperationStatus extends AssemblyOperationStatus{
  def text = "busy"
}
case object FreeOperationStatus extends AssemblyOperationStatus{
  def text = "free"
}

object AssemblyOperationStatus{
  def apply(text:String) ={
    text match {
      case "busy" => BusyOperationStatus
      case "free" => FreeOperationStatus
      case "q" => FreeOperationStatus
    }
  }
}

case class AssemblyOperation(operation:Operation,time:Int,status:AssemblyOperationStatus)

case class Assembly(id:Int,name:String,totalOperations:List[AssemblyOperation] =List[AssemblyOperation](),
                    allocatedOperations:List[AssemblyOperation]) {

//  def freeOperation(maybeOperation: Option[Operation]):Unit ={
//    maybeOperation match{
//      case Some(x) => {
//        allocatedOperations -= x
//      }
//      case None =>
//    }
//  }
//
//  def allocateOperation(operation: Operation): Boolean ={
//    synchronized {
//      totalOperations.filter(_.operation.equals(operation)).contains(operation) match {
//        case true =>
//          allocatedOperations += operation
//          true
//        case false => false
//      }
//    }

//  }
}
