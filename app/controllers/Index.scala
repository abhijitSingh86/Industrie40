package controllers

import javax.inject.Inject

import db.DbModule
import json.DefaultRequestFormat
import models.{Assembly, Component, Operation, Simulation}
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import scheduler.SchedulerThread

/**
  * Created by billa on 03.01.17.
  */
class Index @Inject()(ws:WSClient,db:DbModule)  extends Controller{
//this : AssemblyDaoRepo with SimulationDaoRepo=>


  var schedulerThread:SchedulerThread = null

  def getShellScriptStructure(simualtion:Simulation):String = {
    val assem = simualtion.assemblies.map(x=>{
      s"""xterm -hold -e sbt "run -a ${x.id} -s ${simualtion.id}" &  """
    })

    val comps = simualtion.components.map(x=>{
      s"""xterm -hold -e sbt "run -c ${x.id} -s ${simualtion.id}" &  """
    })
    (assem ++ comps).mkString("\n")
  }

  def simulationStatus() = Action{ implicit request =>
    if(request.queryString.get("body").isDefined) {

      val body = request.queryString.get("body").get.head
      println(body)
      val json = Json.parse(body)
      import factory.JsonImplicitFactory._
      val name = (json \ "simulationName").as[String]
      val desc = (json \ "simulationDesc").as[String]

      val simulationId = (json \ "simulationId").as[Int]

      val compo = (json \ "components").validate[List[Component]].get

      val assem = (json \ "assemblies").validate[List[Assembly]].get

      val simulation = new Simulation(simulationId,name,desc,compo,assem)
      val result = getShellScriptStructure(simulation)

      Ok(views.html.index2(result))
    }else{
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
