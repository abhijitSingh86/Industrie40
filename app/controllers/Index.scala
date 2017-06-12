package controllers

import javax.inject.Inject

import db.DbModule
import json.{DefaultRequestFormat, ResponseFactory, SimulationJson}
import models.{Assembly, Component, Operation, Simulation}
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import scheduler.SchedulerThread
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by billa on 03.01.17.
  */
class Index @Inject()(ws:WSClient,db:DbModule)  extends Controller{

  var schedulerThread:SchedulerThread = null

  def simulationStatus(id:Int) = Action{ implicit request =>
    //get the ID and fetch simulation details
    try{
      //Json.stringify(ResponseFactory.make(SimulationJson(db.getSimulation(id))))
      Ok(views.html.index2(id.toString))
    }catch{
      case _:Throwable =>
      BadRequest("body with valid Simulation Details is Missing")
    }
  }
  def index() =Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def assemblyOperationCompletion() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO

    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    Logger.info("Assemble operation completion request recieved "+json)
    Ok(DefaultRequestFormat.getEmptySuccessResponse())
  }

  def assemblyHeartBeat() = Action.async { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]
    //check for init id and url params
    db.assemblyHeartBeatUpdateAsync(assemblyId,simulationId ) map{
      case x:Boolean  =>
      {
        //        println(s"*************************HeartBeat for cmpId:${componentId} simId:${simulationId}")
        //return OK response
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      }
      case _ =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("Heart Beat update",s"Heart beat update failed for " +
          s"assemblyId:${assemblyId} simId:${simulationId}"))))
    }
  }

  def initAssemblyRequest() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]

    val url =(json.get \ "url").get.as[String]
    //check for init id and url params
    db.getAssemblyMappedToSimulationId(assemblyId,simulationId) match{
      case Some(x)=>
      {
        // In Future -- check the url existence by calling hearbeat check on given url

        //add url into component simulation table
        db.addAssemblyUrlToItsMappingEntry(simulationId,x.id,url) match{
          case true =>
            //return OK response
            Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("id" -> x.id,"simulationId" -> simulationId ,
              "availableOperations" -> x.totalOperations.map(_.operation.id) ,
              "usedOperationRecords" -> JsArray(List.empty) ,
              "toDoRetryOperations" ->JsArray(List.empty) , "toBefailedOperations" -> JsArray(List.empty) )))
          case false=>
            Ok(DefaultRequestFormat.getValidationErrorResponse(List(("error","adding Url failed, try again"))))
        }
      }
      case None =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("assemblyId","provided assembly Id is invalid"))))
    }
  }



}
