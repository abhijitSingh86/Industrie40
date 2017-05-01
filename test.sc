import enums.PriorityEnum
import models.{Component, Operation, ProcessingSequence}
import play.api.libs.json._
import play.api.libs.functional.syntax._

//implicit val opReads = new Reads[Operation]{
//  override def reads(json: JsValue): JsResult[Operation] = {
//    new Operation(name =  (json \ "label").read[String])
//  }
//}

implicit val opRead:Reads[Operation] = (
  (JsPath \ "id").read[Int] and
  (JsPath \ "label").read[String]
)(models.Operation.apply _)

val str = """{"simulationName":"qq","simulationDesc":"ww","operations":[{"id":0,"label":"fixed in Registration omdule"},{"label":"qq","id":1},{"label":"ww","id":2},{"label":"qw","id":3}],"components":[{"id":0,"name":"www","opCount":1,"operationDetails":[[0],["1"]]},{"id":1,"name":"qqq","opCount":2,"operationDetails":[["1",1],["1","3"],["2",1]]}],"assemblies":[{"id":0,"name":"qqq","operationDetails":[{"time":"12","opid":0},{"time":"14","opid":1}]}],"operationCounter":4,"componentCounter":2,"assemblyCounter":1}"""

val json = Json.parse(str)


val op = (json \ "operations").validate[List[Operation]]

op match {
  case s:JsSuccess[Operation] =>
    println(s.get)
  case f:JsError =>
    println(f)
}

implicit val processingSeqReads:Reads[ProcessingSequence] = (

)(ProcessingSequence apply)

implicit val compReads:Reads[Component] = (
  (JsPath \ "id").read[Int] and
  (JsPath \ "name").read[String] and
  (JsPath \ "operationDetails").read[List[ProcessingSequence]]
  )(Component apply(_,_,PriorityEnum.NORMAL,_))