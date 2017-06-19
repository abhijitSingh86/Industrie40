package models

/**
  * Created by billa on 13.05.17.
  */
case class OperationProcessingInfo(operationId:Int,assemblyId:Int,assemblyName:String
                                   ,startTime:Long =0l ,endTime:Long =0l,status:String ="Pass") {

}
