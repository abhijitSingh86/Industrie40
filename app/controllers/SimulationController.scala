package controllers

import java.util.Date

import data.OnlineData
import db.DbModule
import db.generatedtable.Tables
import json._
import models._
import network.NetworkProxy
import play.api.mvc.{Action, Controller, Result}
import scheduler.{ApplicationLevelData, ComponentQueue}
import utils.ComponentUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
/**
  * Created by billa on 16.04.17.
  */

import play.api.libs.json._

class SimulationController(database:DbModule,networkproxy:NetworkProxy) extends Controller {

  def getVersionData(id:Int) = Action.async {
    val ids = database.getSimulationVersions(id)
    ids.map(x=>
    Ok(Json.toJson(x.distinct)))

  }

  def getAllSimulations() = Action {
    Ok(ResponseFactory.make(SimulationsJson(database.getAllSimulation())))
//    Ok(Json.arr(.map(x=> ResponseFactory.make(SimulationJson(x)))))
//    Ok(Json.arr(database.getAllSimulation().map(x=> ResponseFactory.make(SimulationJson(x)))))
  }

  def simulationRunningStatus(simulationId:Int) = Action.async {
    val componentProcessingRows = database.getComponentProcessingInfoForSimulation(simulationId , ComponentQueue.getSimulationVersionId())

    componentProcessingRows.map(rows => {
        val asmIds = rows.map(_.assemblyid).distinct
        val assemblyMap:Map[Int,(Assembly , Seq[Tables.ComponentProcessingStateRow])] = asmIds.map(id => (id ->
          (database.getAssemblyMappedToSimulationId(id,simulationId).get , rows.filter(_.assemblyid==id)))).toMap

      val compIds = rows.map(_.componentid).distinct
      val componentMap: Map[Int, Component] = compIds.map(id => (id ->
                database.getComponentById(id, simulationId,ComponentQueue.getSimulationVersionId(), rows.filter(_.componentid == id)))).toMap


      val completedComponents = componentMap.values.filter(x=>ComponentUtils.isCompleted(x)).map(_.id)

      if(completedComponents.size >0){
        networkproxy.sendKillCompletedComponentToGhostApp(completedComponents.toSeq)
      }

      Ok(ResponseFactory.make(ProcessingStatus(componentMap,assemblyMap,completedComponents.size == OnlineData.getTotalComponents().size)))
      }
      )

  }



  def getSimulation(id:Int,mode:String , version:Int) = Action{
    //All code loading for the
    var response:Option[Result]=None
    //start the Ghost loading
    if(mode.equalsIgnoreCase("start") ){
      if(!ApplicationLevelData.isGhostOnline()){
        response=  Some(BadRequest("Background Ghost app is not running. Not able to start the Simulation monitoring."))
      }
    }

    if(version == -1){
      ComponentQueue.updateSimulationId(id,0)
    }else{
      ComponentQueue.updateSimulationId(id,version)
    }


    if(!response.isDefined) {
      val sim = database.getCompleteSimulationObject(id)
      response = Some(Ok(ResponseFactory.make(SimulationJson(sim))))

      if (mode.equalsIgnoreCase("start")) {
        OnlineData.setSimulationData(sim)
        OnlineData.resetOnlineData()
        OnlineData.setTotalComponentCount(sim.components.size+sim.assemblies.size);
        networkproxy.sendStartToGhostApp(sim)
      }
    }
    response.get
  }

  def getAssemblyTimelineDetails(simulationid:Int) = Action.async {
    val rows = database.getComponentProcessingInfoForSimulation(simulationid,ComponentQueue.getSimulationVersionId())
    val failureDetails = database.getAssemblyFailureEntries(simulationid,ComponentQueue.getSimulationVersionId())
    val anameMap = database.getAssemblyNameMapForSimulation(simulationid)

    /*
    {
                    "start": new Date(x.startTime), "end": new Date(x.endTime),  // end is optional
                    "content": con, "group": y.name
     */

    val failuregroupDetails = failureDetails.map(_.map(x=>{
      Json.obj("start"->new Date(x.starttime) , "end"->new Date(x.endtime.getOrElse(0l)) , "group"-> anameMap.get(x.assemblyid).get ,
        "content" -> s"under failure for ${x.failureduration}")
    }))

    val groupDetails = rows.map(_.map(x=>{
      Json.obj("start"->new Date(x.startTime) , "end"->new Date(x.endTime.getOrElse(0l)) , "group"-> anameMap.get(x.assemblyid).get ,
      "content" -> s"${x.componentid} with ${x.status}")
    }))

    val finaldataList = for{
      f <-  failuregroupDetails
      g <- groupDetails
    }yield (f ++ g)

    finaldataList.map(x=> Ok(Json.obj("groups"-> anameMap.map(vv=>Json.obj("id"->vv._2)) , "data" -> x)))

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
        database.incrementSimulationVersionDetails(simulationId.get) map{
          case version:Int => {
            ComponentQueue.updateSimulationId(simulationId.get,version)
            OnlineData.resetOnlineData()
            Ok(DefaultRequestFormat.getEmptySuccessResponse())
          }
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
          val a1 = assemblyIds.toMap.get(x.assembly1).getOrElse(0)
          val a2 = assemblyIds.toMap.get(x.assembly2).getOrElse(0)
          AssemblyTransportTime(a1, a2,x.transportTime)
        })

        database.addAssemblyTimeMap(simulationId,assemblyTTUpdated)

        val componentTT = compToAssemTT.map(x=>{
          ComponentToAssemblyTransTime(assemblyIds.toMap.get(x.assembly).get , componentIds.toMap.get(x.component).get,x.transportTime )
        })

        database.addComponentTimeMap(simulationId,componentTT)

        //save json data in database for cloning purpose

        database.saveJsoninDatabaseforClone(simulationId,Json.stringify(json))
      }
      case f:JsError =>
        println(f)
    }

    val jsonRes = Json.obj("s"->Json.obj("id" -> simulationId,"versionId"->1))

    Ok(DefaultRequestFormat.getSuccessResponse(jsonRes))
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



  def getCloneData(simulationId:Int) = Action {
    Ok(database.getJsonFromCloneDatabase(simulationId))
  }


}
