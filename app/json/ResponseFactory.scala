package json

import models.{Component, Simulation}
import play.api.libs.json.{JsArray, JsValue, Json}
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

