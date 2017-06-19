package utils

import models.Component

object ComponentUtils {

  def isCompleted(x: Component): Boolean = {
    if (x.componentSchedulingInfo.pastProcessings.size > 0) {
      val seq = x.componentSchedulingInfo.pastProcessings.filter(_.status == "Pass")

      x.processingSequences.foreach(y => {
        val sub = y.seq.slice(0, seq.size)
        if (sub.length == seq.length  && sub.size == x.totalReqdOperationCount) {

         return sub.zip(seq).map(x=>x._1.id == x._2.operationId).fold(true){
            (a,b) => a==b
          }
        }else{
          false
        }
      })
      false
    }
    else {
      false
    }
  }
}
