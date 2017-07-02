package scheduler.commands

import db.DbModule
import models.Component
import network.NetworkProxy
import play.api.Logger
import scheduler.{ComponentQueue, Scheduler}

/**
  * Created by billa on 10.01.17.
  */
class ScheduleCommand(dbModule : DbModule,scheduler:Scheduler,proxy: NetworkProxy) extends Command{


  val logger = Logger("access")
  override def execute(): Unit = {
    logger.info("Schedule command execute command started")
    //retrieve all the assemblies to schedule on

    //retrieve all components from the queue
    val components = ComponentQueue.popAll()
    logger.debug("retrieved Components"+components.mkString(","))
    if(components.size >0) {

      val assemblies = dbModule.getAllAssembliesForSimulation(ComponentQueue.getSimulationId())
      logger.debug("Retrieved Assemblies" + assemblies.mkString(","))
      val filteredBusyAssemblies = assemblies.filterNot(x=>{
        x.allocatedOperations.size > 0
      })
      logger.debug("Filtered  Assemblies  =" + filteredBusyAssemblies.mkString(","))

      val alreadyScheduledList = components.filter(_.getCurrentOperation().isDefined).map(_.id)
      //call algorithm for scheduling
      val scheduledComponentIds = scheduler.scheduleComponents(components.filterNot(x=> alreadyScheduledList.contains(x.id))
        , filteredBusyAssemblies)
      //get scheduled component and send them to network proxy for information sending

      val finalListToSendSchedulingInfo = alreadyScheduledList ::: scheduledComponentIds
      sendScheduleInformationToComponent(ComponentQueue.getSimulationId(), components.filter(x=> finalListToSendSchedulingInfo.contains(x.id)))


      logger.debug("command nearly finished, processed UnScheduled components are " + scheduledComponentIds.mkString(","))
      //add pending if any to the component queue again
      components.filter(x=> !scheduledComponentIds.contains(x.id)).map(ComponentQueue.push(_))
    }
    logger.info("Schedule command execute finished")
  }

  def sendScheduleInformationToComponent(simulationId: Int, components: List[Component]) = {
    val updatedCmps = components.map(x=> dbModule.getComponentWithProcessingInfo(x.id,simulationId)).flatten
    val urls = dbModule.getAllComponentUrlBySimulationId(simulationId).toMap
    val assemblyUrls =dbModule.getAllAssemblyUrlBySimulationId(simulationId).toMap

    val assemblies = dbModule.getAllAssembliesForSimulation(simulationId)
    val assemMap = assemblies.map(x=>(x.id -> x)).toMap
    updatedCmps.map(x => {
      //TODO
      // send request at component attached urls for assembly assignments
      val ass = assemMap.get(x.componentSchedulingInfo.currentProcessing.get.assemblyId).get
      proxy.sendAssemblyDetails(urls.get(x.id).get,ass,assemblyUrls,x.componentSchedulingInfo.currentProcessing.get.operationId)
    })
  }
}
