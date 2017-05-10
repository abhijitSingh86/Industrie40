package controllers

import db.{DBComponent, DbModule}
import db.dao.SimulationDaoRepo
import enums.PriorityEnum
import json.DefaultRequestFormat
import models._
import play.api.libs.iteratee.Enumeratee
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.parsing.json.JSONObject

/**
  * Created by billa on 16.04.17.
  */

import play.api.libs.json._
import play.api.libs.functional.syntax._

import factory.JsonImplicitFactory._

class SimulationController(database:DbModule) extends Controller {


  def getAllSimulations() = Action {
    Ok(DefaultRequestFormat.getSuccessResponse(Json.arr(database.getAllSimulation().map(getJsonStructure(_)))))
  }

  def getSimulation(id:Int) = {
    Ok(DefaultRequestFormat.getSuccessResponse(getJsonStructure(database.getSimulation(id))))
  }

  def getJsonStructure(simualtion:Simulation):JsObject = {
    Json.obj("simulationName" -> simualtion.name,
      "simulationDesc" -> simualtion.desc,
      "components" -> Json.arr(simualtion.components.map(x=> {
        Json.obj("id"-> x.id,
        "name"-> x.name,
        "opCount"->x.processingSequences(0).seq.size,
          "operationDetails" -> Json.arr(x.processingSequences.map(y=>{
            Json.arr(y.seq.map(Json.toJson(_)))
          })))
      })),
      "assemblies" -> Json.arr(simualtion.assemblies.map(x=>{
        Json.obj("id"->x.id,
        "name" -> x.name,
        "operationDetails" -> Json.arr(x.totalOperations.map(y=>{
          Json.obj("id"-> y._1.id,
          "name"-> y._1.name,
            "time"->y._2
          )
        })))
      }))
    )
  }

  def deleteSimulation(id:Int) = TODO


  def addSimulation() = Action{ request =>

    import factory.JsonImplicitFactory._
//    _
    val json= request.body.asJson.get
    val name = (json \ "simulationName").as[String]
    val desc = (json \ "simulationDesc").as[String]

    val simulationId = database.addSimulation(name,desc)

    val op = (json \ "operations").validate[List[Operation]]

    val compo = (json \ "components").validate[List[Component]].get

    val assem = (json \ "assemblies").validate[List[Assembly]].get

    op match {
      case s:JsSuccess[List[Operation]] => {
        val opMap = s.get.map(x => (x.id -> database.addOperation(x))).toMap

        val updatedComps = compo.map(x=> { new Component(x.id,x.name,PriorityEnum.NORMAL,
          (x.processingSequences.map(y=> new ProcessingSequence(y.seq.map(a=> new Operation(opMap(a.id),a.name)))))
        )
        })

        val componentIds = updatedComps.map(x => {
           database.addComponent(x)
        })

        database.addComponentsToSimulation(simulationId,componentIds)

        val assemblyIds = assem.map(x=> x.copy(totalOperations = x.totalOperations.map(y => (new Operation(opMap(y._1.id),y._1.name),y._2)
        ))).map(database.addAssembly(_))

        database.addAssembliesToSimulation(simulationId,assemblyIds)


      }
      case f:JsError =>
        println(f)
    }






    Ok(DefaultRequestFormat.getEmptySuccessResponse())
  }

//  def updateSimulation() = Action.async{
//    request =>
//
//      ApiResponse{
//        for{
//          simulation <- validationHandler(request)
//          id <- update
//        }
//      }
//  }


  def jsonExtractor(request:Request[AnyContent]):Future[Simulation] = {

    val json = request.body.asJson

    //Extracting Simulation

    val name= (json.get \ "simulationName").get.as[String]
    val desc= (json.get \ "simulationDesc").get.as[String]

    val simulation = new Simulation(id=0,name=name,desc = desc)
    //Extracting Operations
    


   Future.successful(new Simulation(id=0,name=name,desc = desc))
  }

}
