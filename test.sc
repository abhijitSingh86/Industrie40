//import java.sql.Timestamp
import java.time.{Duration, LocalDate, LocalDateTime, Period}
import java.util.{Calendar, Date}
import java.util.concurrent.TimeUnit

import sun.util.resources.LocaleData
import utils.DateTimeUtils

import scala.collection.mutable

//import scala.collection.mutable
//
//val li:mutable.ListBuffer[Int] = mutable.ListBuffer(1,2)
//li :+ 3

val list = List(8,1,2,3,4,5,6,7)

list.sortWith(_ < _)



//val q = new mutable.Queue[Int]
//q += 1
//q +=2
//
//q.filterNot(_ ==2)
//val l =new  mutable.LinkedHashSet[Int]()
//l +=1
//l +=2
//l
//l+=2
//l


//
//val m = List((1->List(1,2,3)) , (2->List(1,2,5)) , (3->List(4,5,6))).toMap
//val m1 = List((1->List(2)) , (2->List(1))).toMap
//
//m1++ m
//m ++ m1
//
//
//val l = List(1,2,4,2,3,4,5,6)
//l.filterNot(_ == 1)
//l.distinct
//l.sortWith(_ > _)


//val st:Long = 1495580454000l
//val et:Long = 1495580479000l
//val diff = et-st
//val c  = Calendar.getInstance()
//c.add(Calendar.SECOND,-7)
//val s= new Timestamp(c.getTimeInMillis)
//val c1 =new Timestamp(Calendar.getInstance().getTimeInMillis)
//
//s.before(c1)
//
//c1.s
//
//
//TimeUnit.SECONDS.convert(et-st , TimeUnit.MILLISECONDS)

//Duration.between(new Date(et),new Date(st))

//Period.between(LocalDateTime.ofEpochDay(et),
  //LocalDate.ofEpochDay(st))


//  .compareTo(DateTimeUtils.getCurrentTimeStamp())






//http://localhost:9000/simulationStatus?body={%22simulationId%22:16,%22simulationName%22:%22abhi%22,%22simulationDesc%22:%22test%22,%22components%22:[{%22id%22:42,%22name%22:%22c1%22,%22opCount%22:3,%22operationDetails%22:[[{%22id%22:67,%22label%22:%22o1%22},{%22id%22:68,%22label%22:%22o2%22},{%22id%22:69,%22label%22:%22o3%22}],[{%22id%22:68,%22label%22:%22o2%22},{%22id%22:67,%22label%22:%22o1%22},{%22id%22:69,%22label%22:%22o3%22}]]},{%22id%22:43,%22name%22:%22c2%22,%22opCount%22:2,%22operationDetails%22:[[{%22id%22:66,%22label%22:%22fixed%20in%20Registration%20omdule%22},{%22id%22:67,%22label%22:%22o1%22}],[{%22id%22:67,%22label%22:%22o1%22},{%22id%22:66,%22label%22:%22fixed%20in%20Registration%20omdule%22}]]}],%22assemblies%22:[{%22id%22:26,%22name%22:%22a1%22,%22operationDetails%22:[{%22id%22:66,%22name%22:%22fixed%20in%20Registration%20omdule%22,%22time%22:20},{%22id%22:67,%22name%22:%22o1%22,%22time%22:30}]},{%22id%22:27,%22name%22:%22a2%22,%22operationDetails%22:[{%22id%22:67,%22name%22:%22o1%22,%22time%22:23},{%22id%22:68,%22name%22:%22o2%22,%22time%22:21},{%22id%22:69,%22name%22:%22o3%22,%22time%22:20}]}]}
//
//val m = mutable.Map("s" ->3,"a"-> 4)
//
//val q = m + ("q"->5)
//
//m += ("f"->5)
//
//q
//
//m
//
////val list = List()
//val list = List(5,4,3,4,5)
//
//list.sortWith(_ < _)
//
//list.filter(_ == 4)
//
//list.drop(1)
//list.takeRight(list.length-1)

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
