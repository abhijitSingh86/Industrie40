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


  val logger = Logger(this.getClass())
  override def execute(): Unit = {
    logger.info("Schedule command execute command started")
    //retrieve all the assemblies to schedule on

    //retrieve all components from the queue
    val components = ComponentQueue.popAll()
    logger.debug("retrieved Components"+components.mkString(","))
    if(components.size >0) {

      val assemblies = dbModule.getAllAssembliesForSimulation(ComponentQueue.getSimulationId())
      logger.debug("Retrieved Assemblies" + assemblies.mkString(","))


      //call algorithm for scheduling
      val unscheduledComponents = scheduler.scheduleComponents(components, assemblies)
      //get scheduled component and send them to network proxy for information sending
      sendScheduleInformationToComponent(ComponentQueue.getSimulationId(), components.filter(!unscheduledComponents.contains(_)))


      logger.debug("command nearly finished, processed UnScheduled components are " + unscheduledComponents.mkString(","))
      //add pending if any to the component queue again
      unscheduledComponents.map(ComponentQueue.push(_))
    }
    logger.info("Schedule command execute finished")
  }

  def sendScheduleInformationToComponent(simulationId: Int, components: List[Component]) = {
    val urls = dbModule.getAllComponentUrlBySimulationId(simulationId).toMap
    val assemblyUrls =dbModule.getAllAssemblyUrlBySimulationId(simulationId).toMap
    components.map(x => {
      // attach assembly in simulationAssemblyMapping
      dbModule.assignAssemblytoComponentSimulationMapping(x.getCurrentAllocatedAssembly().get.id,x.id,simulationId)
      // send request at component attached urls for assembly assignments
      proxy.sendAssemblyDetails(urls.get(x.id).get,x.getCurrentAllocatedAssembly().get,assemblyUrls,x.getCurrentOperation().get)
    })
  }
}
