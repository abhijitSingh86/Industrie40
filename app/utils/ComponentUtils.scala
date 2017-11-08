package utils

import models.{Component, ComponentProcessingStatus, FinishedProcessingStatus}

object ComponentUtils {

  def isCompleted(x: Component): Boolean = {
    if (x.componentSchedulingInfo.pastProcessings.size > 0) {
      val seq = x.componentSchedulingInfo.pastProcessings.filter(x=>x.status == FinishedProcessingStatus.text)

      x.processingSequences.map(y => {
        val sub = y.seq.slice(0, seq.size)
        if (sub.length == seq.length  && sub.size == x.totalReqdOperationCount) {
          //Zip the sub list and the processing list and compare each element to find the correct Execution Sequence number
         val correctSeqFound =sub.zip(seq).map(x=>x._1.id == x._2.operationId).fold(true){
            (a,b) => a==b
          }
          correctSeqFound
        }else{
          false
        }
      }).contains(true)
    }
    else {
      false
    }
  }
}
