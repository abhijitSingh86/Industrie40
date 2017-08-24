package controllers

import db.DbModule
import json.{ComponentWithSchedulingInfo, DefaultRequestFormat, ResponseFactory}
import models.Component
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scheduler.ComponentQueue
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by billa on 09.01.17.
  */
class ComponentController(db:DbModule) extends Controller {

  def updateComponentCompletionTime() = Action{ implicit request =>
    val json = request.body.asJson
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]
    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    val operationId = (json.get \ "operationId").get.as[Int]
    val sequence= (json.get \ "sequence").get.as[Int]
    val failureWaitTime= (json.get \ "failureWaitTime").get.as[Int]
    db.updateComponentProcessingInfo(simulationId,componentId,assemblyId,sequence , operationId,failureWaitTime) match {
      case true => {


        //Add to next scheduling
        db.getComponentWithProcessingInfo(componentId,simulationId ) match{
          case Some(x)  =>
          {
            if(!x.isComplete())
              ComponentQueue.push(x)
          }
        }
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      }
      case false => Ok(DefaultRequestFormat.getValidationErrorResponse(
        List(("ComponentProcessingInfo","Component Processing record not found"))))
    }
  }

  def updateComponentOperationFailure() = Action{ implicit request =>
    val json = request.body.asJson
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]
    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    val operationId = (json.get \ "operationId").get.as[Int]
    val sequence= (json.get \ "sequence").get.as[Int]
    db.updateComponentProcessingInfoInFailureScenarion(simulationId,componentId,assemblyId,sequence , operationId) match {
      case true => Ok(DefaultRequestFormat.getEmptySuccessResponse())
      case false => Ok(DefaultRequestFormat.getValidationErrorResponse(
        List(("ComponentProcessingInfo","Component Processing record not found"))))
    }
  }

  def initComponentRequest() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]

    val url =(json.get \ "url").get.as[String]
    //check for init id and url params
    db.getComponentWithProcessingInfo(componentId ,simulationId) match{
      //check if this exist for simulation ID
      case Some(x) =>
      {
        // In Future -- check the url existence by calling hearbeat check on given url
        //add url into component simulation table
        db.addComponentUrlToSimulationMapEntry(simulationId,x.id,url) match{
          case true =>
            //return OK response
            Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("id" -> x.id,"name" -> x.name ,
              "totalOperationCount" -> x.totalReqdOperationCount , "completedOperationCount"->x.componentSchedulingInfo.completedOperations.length)))
          case false=>
            Ok(DefaultRequestFormat.getValidationErrorResponse(List(("error","adding Url failed, try again"))))
        }
      }
      case None =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("componentId","provided component Id is invalid"))))
      case Some(x) =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("componentId","provided component Id is not in Simulation Id"))))
    }
  }


  def componentRunningStatus(cmpId:Int,simId:Int)
  = Action {
    // get all the details from server
    //make json response
      val response  = db.getComponentWithProcessingInfo(cmpId,simId) match {
        case Some(x:Component) => ResponseFactory.make(ComponentWithSchedulingInfo(x))
        case None => DefaultRequestFormat.getValidationErrorResponse(List(("Data Error","Component Id doesn't Exist")))
      }
    Ok(response)
  }

  def startComponentScheduling() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]

    if(ComponentQueue.requestQueue.filter(_.id == componentId).size >0){
      Ok(DefaultRequestFormat.getEmptySuccessResponse())
    }
    //check for init id and url params
    db.getComponentWithProcessingInfo(componentId,simulationId ) match{
      case Some(x)  =>
      {
        ComponentQueue.push(x)
        //return OK response
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      }
      case None =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("componentId","provided component Id is invalid"))))
    }
  }

  def componentHeartBeat() = Action.async { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]
    //check for init id and url params
    db.componentHeartBeatUpdateAsync(componentId,simulationId ) map{
      case x:Boolean  =>
      {
//        println(s"*************************HeartBeat for cmpId:${componentId} simId:${simulationId}")
        //return OK response
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      }
      case _ =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("Heart Beat update",s"Heart beat update failed for " +
          s"cmpId:${componentId} simId:${simulationId}"))))
    }
  }
}
