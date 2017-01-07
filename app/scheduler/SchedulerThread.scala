package scheduler

import db.MySqlDBComponent
import db.dao.{AssemblyDao, SlickAssemblyDAO, SlickOperationDao, SlickSimulationDao}
import network.NetworkProxy

/**
  * Created by billa on 07.01.17.
  */
class SchedulerThread(private val simulationId:Int,proxy:NetworkProxy )  extends Runnable{

  val sleepTime = 5000
  val assemblyDao:AssemblyDao = new SlickAssemblyDAO with MySqlDBComponent with SlickOperationDao
  val simulationDao = new SlickSimulationDao  with MySqlDBComponent
  val scheduler:Scheduler = new ComponentScheduler

  override def run(): Unit = {
    while(true){
      //Sleep for a while before scheduling
      try{
        Thread.sleep(sleepTime)
      }
      catch{
        case ex:Exception => {
          print("error occurred while Sleeping thread")
        }
      }

      //retrieve all the assemblies to schedule on
      assemblyDao.selectBySimulationId(simulationId)
      val assemblies = assemblyDao.selectBySimulationId(simulationId)
      //retrieve all components from the queue
      val components = ComponentQueue.popAll()

      //call algorithm for scheduling
      val unscheduledComponents = scheduler.scheduleComponents(components,assemblies)
      //get scheduled component and send them to network proxy for information sending
      proxy.sendScheduleInformationToComponent(simulationId,components.filter(unscheduledComponents.contains(_)))



      //add pending if any to the component queue again
      unscheduledComponents.map(ComponentQueue.push(_))
    }




  }
}
