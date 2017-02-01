package scheduler.commands

import db.dao.{AssemblyDao, SlickSimulationDao}
import network.NetworkProxy
import play.api.Logger
import scheduler.{ComponentQueue, Scheduler}

/**
  * Created by billa on 10.01.17.
  */
class ScheduleCommand(private val simulationId:Int,proxy:NetworkProxy,assemblyDao:AssemblyDao ,
                      simulationDao:SlickSimulationDao,scheduler: Scheduler) extends Command{

  val logger = Logger(this.getClass())
  override def execute(): Unit = {
    logger.info("Schedule command execute command started")
    //retrieve all the assemblies to schedule on

    //retrieve all components from the queue
    val components = ComponentQueue.popAll()
    logger.debug("retrieved Components"+components.mkString(","))
    if(components.size >0) {

      val assemblies = assemblyDao.selectBySimulationId(simulationId)
      logger.debug("Retrieved Assemblies" + assemblies.mkString(","))


      //call algorithm for scheduling
      val unscheduledComponents = scheduler.scheduleComponents(components, assemblies)
      //get scheduled component and send them to network proxy for information sending
      proxy.sendScheduleInformationToComponent(simulationId, components.filter(!unscheduledComponents.contains(_)))


      logger.debug("command nearly finished, processed UnScheduled components are " + unscheduledComponents.mkString(","))
      //add pending if any to the component queue again
      unscheduledComponents.map(ComponentQueue.push(_))
    }
    logger.info("Schedule command execute finised")
  }
}
