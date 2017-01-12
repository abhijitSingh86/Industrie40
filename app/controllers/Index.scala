package controllers

import javax.inject.Inject

import db.MySqlDBComponent
import db.dao._
import json.DefaultRequestFormat
import network.NetworkProxy
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

  def start() =Action{

    //initialize Database DAOs
    val componentDao:ComponentDao = new SlickComponentDAO  with SlickOperationDao with MySqlDBComponent
    val operationDao:SlickOperationDao  = new SlickOperationDao with MySqlDBComponent
    val assemblyDao = new SlickAssemblyDAO with MySqlDBComponent with SlickOperationDao
    val simulationDao = new SlickSimulationDao with MySqlDBComponent

    //add operations

//      def getOp(name:String) = new ComponentOperation(name=name)
//
//      val ids = List(getOp("A"),getOp("B"),getOp("C"),getOp("D")).map(x=> (x.getName() ,
//        new ComponentOperation(operationDao.add(x),x.getName()))).toMap
//
//    //add component with operations
//    val seq1 = ProcessingSequence(List(ids("A"),ids("B"),ids("C"),ids("D")))
//    val seq2 = ProcessingSequence(List(ids("B"),ids("A"),ids("C"),ids("D")))
//
//    val  component = new Component(0,"component1",PriorityEnum.NORMAL, List(seq1,seq2))
//    val  component2 = new Component(0,"componnt2",PriorityEnum.NORMAL, List(ProcessingSequence(List(ids("C"),ids("D"))),ProcessingSequence(List(ids("D"),ids("C")))))
//    val cmp1Id = componentDao.add(component)
//    val cmp2Id  = componentDao.add(component2)
//
//    //add assemblies
//    val assemblyA = new Assembly(0,"assembly1",List(ids("A"),ids("B"),ids("C")).map(x=>new AssemblyOperation(0,x.getName(),1.2f)))
//    val assemblyB = new Assembly(0,"assembly2",List(ids("D"),ids("B")).map(x=>new AssemblyOperation(0,x.getName(),1.2f)))
//    val assemId = assemblyDao.add(assemblyA)
//    val assemId2 = assemblyDao.add(assemblyB)
//
//    //add simulation
//    val simulatio1 = new Simulation(0,"simulation1","dummy simulation db entry")
//    val simId = simulationDao.add(simulatio1)
//
//    //add components in simulation
//    simulationDao.addComponentsToSimulation(simId,List(cmp1Id,cmp2Id))
//
//
//    //add assemblies in simulation with operation time
//    simulationDao.addAssembliesToSimulation(simId,List(assemId,assemId2))
      val simulationIdInDb = 2;

    //start schedular operation


    Ok("Started the server with following component and assembly")
  }


  def index() =Action{
    val proxy = new NetworkProxy(ws) with SlickSimulationDao with MySqlDBComponent
    val assemblyDao:AssemblyDao = new SlickAssemblyDAO with MySqlDBComponent with SlickOperationDao
    val simulationDao = new SlickSimulationDao with MySqlDBComponent
    val command = new ScheduleCommand(2,proxy,assemblyDao,simulationDao,new ComponentScheduler())
    val scheduler = new SchedulerThread(5000,command)
    new Thread(scheduler).start()
    Ok("Starting the scheduler")
  }


  def initAssemblyRequest() = Action { implicit request =>
    val json =request.body.asJson

    //get Component DAO
    val assemblyDao:AssemblyDao = new SlickAssemblyDAO with MySqlDBComponent with SlickOperationDao
    val simulationDao = new SlickSimulationDao with MySqlDBComponent
    val assemblyId = (json.get \ "assemblyId").get.as[Int]
    val simulationId = (json.get \ "simulationId").get.as[Int]

    val url =(json.get \ "url").get.as[String]
    //check for init id and url params
    assemblyDao.selectByAssemblyId(assemblyId ) match{
      //check if this exist for simulation ID
      case Some(x) if simulationDao.isAssemblyMappedToSimulation(simulationId,x.id) =>
      {
        // In Future -- check the url existence by calling hearbeat check on given url
        //add url into component simulation table
        simulationDao.addAssemblyUrlToItsMappingEntry(simulationId,x.id,url) match{
          case true =>
            //return OK response
            Ok(DefaultRequestFormat.getEmptySuccessResponse())
          case false=>
            Ok(DefaultRequestFormat.getValidationErrorResponse(List(("error","adding Url failed, try again"))))
        }
      }
      case None =>
        Ok(DefaultRequestFormat.getValidationErrorResponse(List(("assemblyId","provided assembly Id is invalid"))))
    }
  }



}
