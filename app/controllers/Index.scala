package controllers

import javax.inject.Inject

import db.MySqlDBComponent
import db.dao._
import json.DefaultRequestFormat
import network.NetworkProxy
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import scheduler.commands.ScheduleCommand
import scheduler.{ComponentQueue, ComponentScheduler, SchedulerThread}

/**
  * Created by billa on 03.01.17.
  */
class Index @Inject()(ws:WSClient)  extends Controller{
this : AssemblyDaoRepo with SimulationDaoRepo=>


  var schedulerThread:SchedulerThread = null
  def index() =Action {
    Ok("react")
  }

//  def start() =Action{
//    Logger.info("Start request recieved.. current thread"+schedulerThread)
//
//    val proxy = new NetworkProxy(ws) with SlickSimulationDao with MySqlDBComponent
//    val assemblyDao:AssemblyDao = new SlickAssemblyDAO with MySqlDBComponent with SlickOperationDao
//    val simulationDao = new SlickSimulationDao with MySqlDBComponent
//    val command = new ScheduleCommand(2,proxy,assemblyDao,simulationDao,new ComponentScheduler())
//    schedulerThread = new SchedulerThread(5000,command)
//    SchedulerThread.startExecution(schedulerThread)
//    Logger.info("Schedule thread created.. current thread"+schedulerThread.toString)
//    Ok("Starting the scheduler")
//  }

//  def stop() = Action {
//    Logger.info("Stop request recieved.. current thread"+schedulerThread)
//    if(schedulerThread !=null){
//      SchedulerThread.endExecution(schedulerThread)
//      Logger.info("Stop request processed.. current thread"+schedulerThread.toString)
//    }
//    Ok("Thread Stopped")
//  }


  def assemblyOperationCompletion() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO

    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    Logger.info("Assemble operation completion request recieved "+json)
    Ok(DefaultRequestFormat.getEmptySuccessResponse())
  }

  def initAssemblyRequest() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]

    val url =(json.get \ "url").get.as[String]
    //check for init id and url params
    assembly.selectByAssemblyId(assemblyId ) match{
      //check if this exist for simulation ID
      case Some(x) if simulation.isAssemblyMappedToSimulation(simulationId,x.id) =>
      {
        // In Future -- check the url existence by calling hearbeat check on given url

        //add url into component simulation table
        simulation.addAssemblyUrlToItsMappingEntry(simulationId,x.id,url) match{
          case true =>
            //return OK response
            Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("id" -> x.id,"simulationId" -> simulationId ,
              "availableOperations" -> x.totalOperations.map(_._1.id) ,
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
