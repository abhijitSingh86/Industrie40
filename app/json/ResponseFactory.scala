package json

import models.{Component, Simulation}
import play.api.libs.json.{JsValue, Json}
import factory.JsonImplicitFactory._
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

case class ComponentWithSchedulingInfo(x:Component) extends Response{

  def generate()= {
    Json.obj("id"-> x.id,
      "name"-> x.name,
      "online"->x.isOnline,
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

case class SimulationJson(simualtion:Simulation) extends Response{
  def generate()={
    Json.obj("simulationId"->simualtion.id,
      "simulationName" -> simualtion.name,
      "simulationDesc" -> simualtion.desc,
      "components" -> simualtion.components.map(x=> {
        Json.obj("id"-> x.id,
          "name"-> x.name,
          "opCount"->x.processingSequences(0).seq.size,
          "operationDetails" -> x.processingSequences.map(y=>{
            y.seq.map(Json.toJson(_))
          }))
      }),
      "assemblies" -> simualtion.assemblies.map(x=>{
        Json.obj("id"->x.id,
          "name" -> x.name,
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

