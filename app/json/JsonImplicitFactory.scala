package factory

import dbgeneratedtable.Tables
import enums.PriorityEnum
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._
/**
  * Created by billa on 02.05.17.
  */
object JsonImplicitFactory {


  implicit val opRead: Format[Operation] = (
    (JsPath \ "id").format[Int] and
      (JsPath \ "label").format[String]
    ) (models.Operation.apply _ , unlift(models.Operation.unapply))



//  implicit val componentProcessingRowFormat:Format[Tables.ComponentProcessingStateRow] =
//    (
//      (JsPath  \ "componentid").format[Int] and
//        (__ \ "startTime").for
//    )

  implicit val operationProcessingInfoFormat:Format[OperationProcessingInfo]= Json.format[OperationProcessingInfo]

//  implicit val schedulingInfoFormat:Format[ComponentSchedulingInfo]=
//    for {
//      l <- (__ \ "pastProcessings").format[List[OperationProcessingInfo]]
//      op <- (__ \ "currentProcessing").format[OperationProcessingInfo]
//      s <- (__ \ "sequence").format[Int]
//      o <- (__ \ "completedOperations").format[List[Operation]]
//    } yield(models.ComponentSchedulingInfo _ , unlift(models.ComponentSchedulingInfo.unapply))






    implicit val processingSeqReads: Reads[List[ProcessingSequence]] =
      (__.read[List[List[Operation]]].map(x => x.map(ProcessingSequence apply _)))

    implicit val compReads: Reads[Component] = (
      (JsPath \ "id").read[Int] and
        (JsPath \ "name").read[String] and
        (JsPath \ "operationDetails").read[List[ProcessingSequence]]
      ) (Component apply(_, _, _ , EmptySchedulingInfo))


    implicit val assemblyOpReads: Reads[AssemblyOperation] = {
      for {
        a <- (__ \ "id").read[Int]
        b <- (__ \ "label").read[String]
        c <- (__ \ "time").read[Int]
      } yield AssemblyOperation(Operation(a, b), c,FreeOperationStatus)
    }

    implicit val assemblyReads: Reads[Assembly] = {
      for {
        id <- (__ \ "id").read[Int]
        name <- (__ \ "name").read[String]
        od <- (__ \ "operationDetails").read[List[AssemblyOperation]]
      } yield (Assembly apply(id = id, name = name, totalOperations = od,List[AssemblyOperation]()))
    }


}
