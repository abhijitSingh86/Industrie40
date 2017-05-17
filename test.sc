
//val list = List()
val list = List(1,2,3,4,5)
list.take(1)
list.takeRight(list.length-1)

//val v=None
//v.map(_)

//
//import enums.PriorityEnum
//import models.{Assembly, Component, Operation, ProcessingSequence}
//import play.api.libs.json._
//import play.api.libs.functional.syntax._
//
//
//val str1 = """{"simulationName":"abhi","simulationDesc":"test","operations":[{"id":0,"label":"fixed in Registration omdule"},{"label":"o1","id":1},{"label":"o2","id":2},{"label":"o3","id":3}],"components":[{"id":0,"name":"c1","opCount":3,"operationDetails":[[{"label":"o1","id":1},{"label":"o2","id":2},{"label":"o3","id":3}],[{"label":"o2","id":2},{"label":"o1","id":1},{"label":"o3","id":3}]]},{"id":1,"name":"c2","opCount":2,"operationDetails":[[{"id":0,"label":"fixed in Registration omdule"},{"label":"o1","id":1}],[{"label":"o1","id":1},{"id":0,"label":"fixed in Registration omdule"}]]}],"assemblies":[{"id":0,"name":"a1","operationDetails":[{"time":20,"id":0,"label":"fixed in Registration omdule"},{"time":30,"id":1,"label":"o1"}]},{"id":1,"name":"a2","operationDetails":[{"time":23,"id":1,"label":"o1"},{"time":21,"id":2,"label":"o2"},{"time":20,"id":3,"label":"o3"}]}],"operationCounter":4,"componentCounter":2,"assemblyCounter":2}"""
//
//val jsonObj = Json.parse(str1)
//
//
//implicit val opRead: Format[Operation] = (
//  (JsPath \ "id").format[Int] and
//    (JsPath \ "label").format[String]
//  ) (models.Operation.apply _ , unlift(models.Operation.unapply))
//
//val op = (jsonObj \ "operations").validate[List[Operation]]
//
//op match {
//  case s:JsSuccess[Operation] =>
//    println("sdf"+s.get)
//  case f:JsError =>
//    println(f)
//}
//
//implicit val processingSeqReads:Reads[List[ProcessingSequence]] =
//  (__.read[List[List[Operation]]].map(x=> x.map(ProcessingSequence apply _)))
//
//implicit val compReads:Reads[Component] = (
//  (JsPath \ "id").read[Int] and
//  (JsPath \ "name").read[String] and
//  (JsPath \ "operationDetails").read[List[ProcessingSequence]]
//  )(Component apply(_,_,PriorityEnum.NORMAL,_))
//
//val compo = (jsonObj \ "components").validate[List[Component]]
//
//compo match {
//  case s:JsSuccess[Component] =>
//    println(s)
//  case f:JsError =>
//    println("error"+f)
//
//}
//
//implicit val assemblyOpReads:Reads[(Operation,Int)] = {
//  for {
//    a <- (__ \ "id").read[Int]
//    b <- (__ \ "label").read[String]
//    c <- (__ \ "time").read[Int]
//  }yield (Operation(a,b),c)
//}
//
//implicit val assemblyReads:Reads[Assembly] ={
//  for {
//    id <- (__ \ "id").read[Int]
//    name <- (__ \ "name").read[String]
//    od <- (__ \ "operationDetails").read[List[(Operation, Int)]]
//  }yield (Assembly apply(id = id,name=name,totalOperations = od))
//}
//
//val assem = (jsonObj \ "assemblies").validate[List[Assembly]]
//
//assem match {
//  case s:JsSuccess[Assembly] =>
//    println(s.get)
//  case f:JsError =>
//    println(f.get)
//}
