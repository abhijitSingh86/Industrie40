package json

import enums.PriorityEnum
import models.{Assembly, Component, Operation, ProcessingSequence}
import play.api.libs.json._
import play.api.libs.functional.syntax._
/**
  * Created by billa on 02.05.17.
  */
object JsonImplicits {


   class PlayJsonImplicits {

    implicit val opRead: Reads[Operation] = (
      (JsPath \ "id").read[Int] and
        (JsPath \ "label").read[String]
      ) (models.Operation.apply _)


    implicit val processingSeqReads: Reads[List[ProcessingSequence]] =
      (__.read[List[List[Operation]]].map(x => x.map(ProcessingSequence apply _)))

    implicit val compReads: Reads[Component] = (
      (JsPath \ "id").read[Int] and
        (JsPath \ "name").read[String] and
        (JsPath \ "operationDetails").read[List[ProcessingSequence]]
      ) (Component apply(_, _, PriorityEnum.NORMAL, _))


    implicit val assemblyOpReads: Reads[(Operation, Int)] = {
      for {
        a <- (__ \ "id").read[Int]
        b <- (__ \ "label").read[String]
        c <- (__ \ "time").read[Int]
      } yield (Operation(a, b), c)
    }

    implicit val assemblyReads: Reads[Assembly] = {
      for {
        id <- (__ \ "id").read[Int]
        name <- (__ \ "name").read[String]
        od <- (__ \ "operationDetails").read[List[(Operation, Int)]]
      } yield (Assembly apply(id = id, name = name, totalOperations = od))
    }

  }
}
