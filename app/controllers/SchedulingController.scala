package controllers

import play.api.Logger
import play.api.mvc.{Action, Controller}
import scheduler.{ComponentQueue, SchedulerThread}
import scheduler.commands.Command

import scala.util.Try

/**
  * Created by billa on 25.04.17.
  */
class SchedulingController(schedulingThread:SchedulerThread)  extends Controller{


  def start()=Action{ implicit request =>
    val json =request.body.asJson
    val simulationId = Try(2)//Try((json.get \ "simulationId").get.as[Int])
    simulationId.isSuccess match{
      case true =>
        ComponentQueue.updateSimulationId(simulationId.get)
        schedulingThread.startExecution()
        Logger.info("Schedule thread created")
        Ok("Starting the scheduler")
      case _=> Ok("simulation Id is not found in Json")
    }
  }

  def stop() = Action {
        Logger.info("Stop request recieved..")
        if(schedulingThread !=null){
          schedulingThread.endExecution()
          Logger.info("Stop request processed.. ")
        }
        Ok("Thread Stopped")
      }

}
