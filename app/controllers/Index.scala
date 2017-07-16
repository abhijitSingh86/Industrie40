package controllers

import javax.inject.Inject

import db.DbModule
import db.generatedtable.Tables
import json.{ComponentWithSchedulingInfo, DefaultRequestFormat, ResponseFactory, SimulationJson}
import models.{Assembly, Component, Operation, Simulation}
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import scheduler.SchedulerThread

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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


  def assemblyRunningStatus(asmId:Int,simId:Int) = Action.async {
    //get Assembly processing record
    val assembly = db.getAssemblyMappedToSimulationId(assemblyId = asmId,simulationId = simId)
    val componentNameMap = db.getComponentNameMapBySimulationId(simId)
    def makeAssemblyResponse(list:Seq[Tables.ComponentProcessingStateRow],componentNameMap:Map[Int,String]) = {

      Json.obj("id"->asmId,"operations"->
      assembly.get.totalOperations.map(f => {
        var past:mutable.ListBuffer[(Int,String)] = mutable.ListBuffer()
        var current:Option[(Int,String)]=None
        //get current and past processing for the operation details
        list.filter(_.operationid == f.operation.id).map(o => if(o.endTime.isDefined){
           past = past :+ (o.componentid,componentNameMap.get(o.componentid).getOrElse(""))
        }else{
          current = Some((o.componentid,componentNameMap.get(o.componentid).getOrElse("")))
        })
        //make json object and append
        Json.obj("op_id" -> f.operation.id , "currentOpDetails" -> Json.obj(
          "past" -> past.map(temp => Json.obj("cmp_id"->temp._1,"cmp_name"->temp._2)),
          "current" -> Json.obj("cmp_id"->current.getOrElse((0,""))._1,"cmp_name"->current.getOrElse((0,""))._2)
        ))
      })
      )
    }

    assembly match{
      case Some(obj) => {
        db.getAssemblyRunningStatus(asmId,simId).map(x=>
          Ok(makeAssemblyResponse(x , componentNameMap))
        )
      }
      case None => Future.successful(Ok(DefaultRequestFormat.getValidationErrorResponse(
        List(("Data Error","Assembly Id doesn't Exist")))))
    }
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
