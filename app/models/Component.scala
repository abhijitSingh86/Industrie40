package models

import enums.PriorityEnum.PriorityEnum
import enums.StateEnum
import enums.StateEnum.StateEnum

import scala.collection.mutable

/**
  * Created by billa on 2016-12-15.
  */

sealed trait ComponentProcessingStatus{
  def text:String
}

case object FinishedProcessingStatus extends ComponentProcessingStatus{
  def text = "finished"
}
case object InProgressProcessingStatus extends ComponentProcessingStatus{
  def text = "InProgress"
}

case object FailedProcessingStatus extends ComponentProcessingStatus{
  def text = "failed"
}


object ComponentProcessingStatus{
  def apply(text:String) ={
    text match {
      case "finished" => FinishedProcessingStatus
      case "InProgress" => InProgressProcessingStatus
      case "failed" => FailedProcessingStatus
      case _ => FinishedProcessingStatus
    }
  }

  def unapply(arg: ComponentProcessingStatus): String = arg.text
}
case class Component(id: Int, name: String, processingSequences: List[ProcessingSequence] ,
                      componentSchedulingInfo:SchedulingInfo,isOnline:Boolean =false) {

  def getCurrentOperation() = componentSchedulingInfo.currentProcessing


  def isComplete(): Boolean={
    if(componentSchedulingInfo.sequence >= totalReqdOperationCount) {
      true
    }else{
      false
    }
  }
  def totalReqdOperationCount = processingSequences.size match{
    case 0 => 0
    case _ => processingSequences(0).seq.size
  }

  /*iterate through the list of completed operation and extracts the list of matching seqeunce with completedoperation. This
   way the next operation will be from the matching seq
   completed = a,b
   processingseq = s1-> a,b,c,d
                  S2 -> b,a,c,d
                  S3 -> a,b,d,c

                  Function will return c from S1 and d from S3... S2 will be discarded because of the insufficient flow.

   */
  def getCurrentProcessingStepOptions(): Seq[Operation] = {
      processingSequences.map(x => {
        componentSchedulingInfo.completedOperations == x.seq.take(componentSchedulingInfo.sequence) match {
          case true => Some(x.seq(componentSchedulingInfo.sequence))
          case _ => None
        }
      }).flatten
    }

}
