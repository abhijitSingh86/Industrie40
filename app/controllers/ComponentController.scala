package controllers

import db.MySqlDBComponent
import db.dao.{ComponentDao, SlickComponentDAO, SlickOperationDao, SlickSimulationDao}
import json.DefaultRequestFormat
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scheduler.ComponentQueue

/**
  * Created by billa on 09.01.17.
  */
class ComponentController extends Controller with SlickSimulationDao with SlickComponentDAO with SlickOperationDao with MySqlDBComponent{


  def initComponentRequest() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]

    val url =(json.get \ "url").get.as[String]
    //check for init id and url params
      selectByComponentId(componentId ) match{
      //check if this exist for simulation ID
      case Some(x) if isComponentMappedToSimulation(simulationId,x.id) =>
      {
        // In Future -- check the url existence by calling hearbeat check on given url
        //add url into component simulation table
        addComponentUrlToItsMappingEntry(simulationId,x.id,url) match{
          case true =>
            //return OK response
            Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("id" -> x.id,"name" -> x.name , "totalOperationCount" -> x.totalReqdOperationCount)))
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

  def startComponentScheduling() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val componentId = (json.get \ "componentId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]
    //check for init id and url params
    selectByComponentId(componentId ) match{
      //check if this exist for simulation ID
      case Some(x) if isComponentMappedToSimulation(simulationId,x.id) =>
      {
        ComponentQueue.push(x)
        //return OK response
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      }
      case None =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("componentId","provided component Id is invalid"))))
    }
  }
}
