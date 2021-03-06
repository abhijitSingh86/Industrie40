package scheduler.commands

import actor.FailureActor.GetFailedAssembly
import actor.FailureGeneratorActor
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import db.DbModule
import models.{Assembly, Component}
import network.NetworkProxy
import play.api.Logger
import scheduler.{ComponentQueue, Scheduler}
import akka.pattern.ask

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import akka.util.Timeout
import utils.ComponentUtils
/**
  * Created by billa on 10.01.17.
  */
class ScheduleCommand(dbModule : DbModule,scheduler:Scheduler,proxy: NetworkProxy) extends Command{

  val logger = Logger("access")
  override def execute(): Unit = {
    logger.info("Schedule command execute command started")
    //retrieve all the assemblies to schedule on

    //retrieve all components from the queue
    val componentIds = ComponentQueue.popAll()

//    dbModule.get
    val components = dbModule.getComponentsWithProcessingInfo(componentIds,ComponentQueue.getSimulationId() , ComponentQueue.getSimulationVersionId()).getOrElse(List())

    logger.debug("retrieved Components"+components.mkString(","))
    if(components.size >0) {

      println("++++++++++++++++++++++++++++++++++++")
      println(ComponentQueue.failedAssemblyId +"  :  "+ComponentQueue.failTime)
      println("++++++++++++++++++++++++++++++++++++")

      val assemblies = dbModule.getAllAssembliesForSimulation(ComponentQueue.getSimulationId())
      logger.debug("Retrieved Assemblies" + assemblies.mkString(","))
      val filteredBusyAssemblies = assemblies.filterNot(x=>{
        x.allocatedOperations.size > 0 || x.id == ComponentQueue.failedAssemblyId
      })

      logger.debug("Filtered  Assemblies  =" + filteredBusyAssemblies.mkString(","))

      val alreadyCompletedComponents = components.filter(x=>ComponentUtils.isCompleted(x)).map(_.id)
      //TODO test this calling IMPORTATNT
      val alreadyScheduledList = components.filter(_.getCurrentOperation().isDefined).map(_.id)
      //call algorithm for scheduling
      val scheduledComponentIds = scheduler.scheduleComponents(components.filterNot(x=>
        alreadyScheduledList.contains(x.id) || alreadyCompletedComponents.contains(x.id))
        , filteredBusyAssemblies)
      //get scheduled component and send them to network proxy for information sending

      //TODO test this calling IMPORTATNT
      //val finalListToSendSchedulingInfo = alreadyScheduledList ::: scheduledComponentIds
      //TODO test this calling IMPORTATNT
      sendScheduleInformationToComponent(ComponentQueue.getSimulationId(), components.filter(x=> scheduledComponentIds.contains(x.id)))
      ComponentQueue.updateInProcess(scheduledComponentIds)


      logger.debug("command nearly finished, processed Scheduled components are " + scheduledComponentIds.mkString(","))
      //add pending if any to the component queue again
      components.filterNot(x=>scheduledComponentIds.contains(x.id) || x.isComplete()).map(x=>ComponentQueue.push(x.id))
    }
    logger.info("Schedule command execute finished")
  }



  def sendScheduleInformationToComponent(simulationId: Int, components: List[Component]) = {
    val updatedCmps = components.map(x=> dbModule.getComponentWithProcessingInfo(x.id,simulationId,ComponentQueue.getSimulationVersionId() )).flatten
    val doubleCheckedMapDbRaceCondi = updatedCmps.map(x=>{
      if(x.getCurrentOperation().isEmpty){
        logger.info(s"***** FOUND AN SCHEULED COMPONENT WITH EMPTY OP compId:${x.id} simId:${simulationId} version:${ComponentQueue.getSimulationVersionId()}")
        dbModule.getComponentWithProcessingInfo(x.id,simulationId,ComponentQueue.getSimulationVersionId() ).get
      }else{
        x
      }
    })
    val urls = dbModule.getAllComponentUrlBySimulationId(simulationId).toMap
    val assemblyUrls =dbModule.getAllAssemblyUrlBySimulationId(simulationId).toMap

    val assemblies = dbModule.getAllAssembliesForSimulation(simulationId)
    val assemMap = assemblies.map(x=>(x.id -> x)).toMap
    doubleCheckedMapDbRaceCondi.map(x => {
      //TODO
      try {
      // send request at component attached urls for assembly assignments
      val ass = assemMap.get(x.componentSchedulingInfo.currentProcessing.get.assemblyId).get
      //Calculate Transport Time
      val transportTime  = ComponentQueue.getTransportTime(x,ass)

      logger.info("**********Transport Time is "+transportTime)

        proxy.sendAssemblyDetails(urls.get(x.id).get, ass, assemblyUrls, x.componentSchedulingInfo.currentProcessing.get.operationId, transportTime)
      }catch {
        case t:Throwable =>{
          logger.error("$$$$$$$$$$$$$$$$Error in Sending scheduling inmfo",t)
        }
      }
    })
  }
}
