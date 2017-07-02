package json

import dbgeneratedtable.Tables
import models.{Assembly, Component, Simulation}
import play.api.libs.json.{JsArray, JsValue, Json}
import factory.JsonImplicitFactory._
import utils.{ComponentUtils, DateTimeUtils}

import scala.collection.mutable
/**
  * Created by billa on 31.05.17.
  */
object ResponseFactory {


  def make(responseType:Response): JsValue ={
    DefaultRequestFormat.getSuccessResponse(responseType.generate())
  }
}

sealed trait Response{
  def generate():JsValue
}


//case class AssemblyProcessingInfo(x:Tables.ComponentProcessingStateRow) extends Response{
//
//  def generate()= {
//    Json.obj("id"-> x.id,
//      "name"-> x.name,
//      "online"->x.isOnline,
//      "isComplete" -> ComponentUtils.isCompleted(x),
//      "opCount"->x.processingSequences(0).seq.size,
//      "operationDetails" -> x.processingSequences.map(y=>{
//        y.seq.map(Json.toJson(_))
//      }),
//      "schedulinginfo" -> Json.obj(
//        "pastOperations" -> Json.toJson(x.componentSchedulingInfo.pastProcessings),
//        "currentOperation" -> Json.toJson(x.componentSchedulingInfo.currentProcessing)
//      ))
//  }
//}

case class ProcessingStatus(cmps:Map[Int,Component] , asms:Map[Int,(Assembly,Seq[Tables.ComponentProcessingStateRow])]) extends Response{
  def generate() = {
    val componentNameMap = cmps.values.map(x=>(x.id->x.name)).toMap
    Json.obj("components" -> cmps.values.map(ComponentWithSchedulingInfo(_).generate()),
    "assemblies"-> asms.values.map(x=>AssemblySchedulingInfo(x._1,componentNameMap,x._2).generate()))
  }
}

case class AssemblySchedulingInfo(assembly:Assembly,componentNameMap:Map[Int,String],list:Seq[Tables.ComponentProcessingStateRow]) extends Response{
  override def generate(): JsValue = {

    def checkAndGet[U,T](x:Option[U] ,value:T ,call: (U)=>T):T = {
        x match{
          case Some(c) => call(c)
          case None => value
        }
    }
    def tableRow(x:Tables.ComponentProcessingStateRow):JsValue= {
      val et = checkAndGet(x.endTime,0L,(x:java.sql.Timestamp)=> x.getTime())
      val st = checkAndGet(x.startTime,0L,(x:java.sql.Timestamp)=> x.getTime())

        Json.obj("componentid"-> x.componentid , "startTime"-> st , "endTime"-> et)
    }

    Json.obj("id"->assembly.id,"operations"->
      assembly.totalOperations.map(f => {
        var past:mutable.ListBuffer[(Tables.ComponentProcessingStateRow,String)] = mutable.ListBuffer()
        var current:Option[(Option[Tables.ComponentProcessingStateRow],String)]=None
        //get current and past processing for the operation details
        list.filter(_.operationid == f.operation.id).map(o => if(o.endTime.isDefined){
          past = past :+ (o,componentNameMap.get(o.componentid).getOrElse(""))
        }else{
          current = Some((Some(o),componentNameMap.get(o.componentid).getOrElse("")))
        })
        //make json object and append
        Json.obj("op_id" -> f.operation.id , "currentOpDetails" -> Json.obj(
          "past" -> past.map(temp => Json.obj("cmp_id"->temp._1.componentid,"row"-> tableRow(temp._1),"cmp_name"->temp._2)),
          "current" -> Json.obj("isPresent"->current.isDefined,"cmp_name"->current.getOrElse((None , ""))._2 ,
            "row"-> current.map(_._1.map(tableRow(_)))
        )))
      })
    )
  }
}
case class ComponentWithSchedulingInfo(x:Component) extends Response{

  def generate()= {
    Json.obj("id"-> x.id,
      "name"-> x.name,
      "online"->x.isOnline,
      "isComplete" -> ComponentUtils.isCompleted(x),
      "opCount"->x.processingSequences(0).seq.size,
      "operationDetails" -> x.processingSequences.map(y=>{
        y.seq.map(Json.toJson(_))
      }),
      "schedulinginfo" -> Json.obj(
        "pastOperations" -> Json.toJson(x.componentSchedulingInfo.pastProcessings),
    "currentOperation" -> Json.toJson(x.componentSchedulingInfo.currentProcessing)
    ))
  }
}
case class SimulationsJson(sims:List[Simulation]) extends Response{
  def generate() = {
//    sims.foldRight(sims,ar:JsArray)((a,b) => b.SimulationJson(a).generate())
    Json.obj("simulations" -> sims.map(x => SimulationJson(x).generate()))
  }
}

case class SimulationJson(simulation:Simulation) extends Response{
  def generate()={
    Json.obj("simulationId"->simulation.id,
      "simulationName" -> simulation.name,
      "simulationDesc" -> simulation.desc,
      "components" -> simulation.components.map(x=> {
        Json.obj("id"-> x.id,
          "name"-> x.name,
          "opCount"->x.processingSequences(0).seq.size,
          "operationDetails" -> x.processingSequences.map(y=>{
            y.seq.map(Json.toJson(_))
          }))
      }),
      "assemblies" -> simulation.assemblies.map(x=>{
        Json.obj("id"->x.id,
          "name" -> x.name,
          "online"->x.isOnline,
          "operationDetails" -> x.totalOperations.map(y=>{
            Json.obj("id"-> y.operation.id,
              "name"-> y.operation.name,
              "time"->y.time
            )
          }))
      })
    )
  }
}

