package controllers

import db.DbModule
import db.generatedtable.Tables
import json._
import models._
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scheduler.ComponentQueue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
/**
  * Created by billa on 16.04.17.
  */

import play.api.libs.json._

class SimulationController(database:DbModule) extends Controller {


  def getAllSimulations() = Action {
    Ok(ResponseFactory.make(SimulationsJson(database.getAllSimulation())))
//    Ok(Json.arr(.map(x=> ResponseFactory.make(SimulationJson(x)))))
//    Ok(Json.arr(database.getAllSimulation().map(x=> ResponseFactory.make(SimulationJson(x)))))
  }

  def simulationRunningStatus(simulationId:Int) = Action.async {
    val componentProcessingRows = database.getComponentProcessingInfoForSimulation(simulationId)

    componentProcessingRows.map(rows => {
        val asmIds = rows.map(_.assemblyid).distinct
        val assemblyMap:Map[Int,(Assembly , Seq[Tables.ComponentProcessingStateRow])] = asmIds.map(id => (id ->
          (database.getAssemblyMappedToSimulationId(id,simulationId).get , rows.filter(_.assemblyid==id)))).toMap

      val compIds = rows.map(_.componentid).distinct
      val componentMap: Map[Int, Component] = compIds.map(id => (id ->
                database.getComponentById(id,simulationId, rows.filter(_.componentid == id)))).toMap

      Ok(ResponseFactory.make(ProcessingStatus(componentMap,assemblyMap)))
      }
      )

  }
  def getSimulation(id:Int) = Action{
    Ok(ResponseFactory.make(SimulationJson(database.getSimulation(id))))
  }

  def getShellScriptStructure(simualtion:Simulation):JsObject = {
    val assem = simualtion.assemblies.map(x=> x.id)

    val comps = simualtion.components.map(x=>x.id)

    Json.obj("c"->comps,"a"->assem,"s"->simualtion.id)
  }

  def startAgain(id:Int)=Action.async { implicit request =>
    val simulationId = Try(id)//Try((json.get \ "simulationId").get.as[Int])
    simulationId.isSuccess match{
      case true =>
        ComponentQueue.updateSimulationId(simulationId.get)
        database.clearPreviousSimulationProcessingDetails(simulationId.get) map{
          case _ =>
            Ok(DefaultRequestFormat.getEmptySuccessResponse())
        }

      case _=> Future.successful(Ok("simulation Id is not found in Json"))
    }
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

    val assemTT = (json \ "assemblyTT").validate[List[AssemblyTransportTime]].get

    val compToAssemTT = (json \ "componentTT").validate[List[ComponentToAssemblyTransTime]].get

    op match {
      case s:JsSuccess[List[Operation]] => {
        val opMap = s.get.map(x => (x.id -> database.addOperation(x))).toMap

        val updatedComps = compo.map(x=> {
          x.copy(processingSequences = x.processingSequences.map(y=> new ProcessingSequence(y.seq.map(a=> new Operation(opMap(a.id),a.name))))
        )
        })

        val componentIds = updatedComps.map(x => {
          x.id ->  database.addComponent(x)
        })

        database.addComponentsToSimulation(simulationId,componentIds.map(_._2))

        val assemblyIds = assem.map(x=> x.copy(totalOperations = x.totalOperations.map(y => new AssemblyOperation(
          new Operation(opMap(y.operation.id),y.operation.name),y.time,FreeOperationStatus))
        )).map(x=> x.id -> database.addAssembly(x))

        database.addAssembliesToSimulation(simulationId,assemblyIds.map(_._2))

        val assemblyTTUpdated = assemTT.map(x=>{
          AssemblyTransportTime(assemblyIds.toMap.get(x.assembly1).get, assemblyIds.toMap.get(x.assembly2).get,
            x.transportTime)
        })

        database.addAssemblyTimeMap(simulationId,assemblyTTUpdated)

        val componentTT = compToAssemTT.map(x=>{
          ComponentToAssemblyTransTime(assemblyIds.toMap.get(x.assembly).get , componentIds.toMap.get(x.component).get,x.transportTime )
        })

        database.addComponentTimeMap(simulationId,componentTT)
      }
      case f:JsError =>
        println(f)
    }

    Ok(DefaultRequestFormat.getSuccessResponse(getShellScriptStructure(database.getSimulation(simulationId))))
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
