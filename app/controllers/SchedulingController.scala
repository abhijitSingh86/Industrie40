package controllers

import db.DbModule
import play.api.Logger
import play.api.mvc.{Action, Controller}
import scheduler.{ComponentQueue, SchedulerThread}
import scheduler.commands.Command
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
  * Created by billa on 25.04.17.
  */
class SchedulingController(schedulingThread:SchedulerThread,db:DbModule)  extends Controller{


  def start(id:Int)=Action{ implicit request =>
    val json =request.body.asJson
    val simulationId = Try(id)//Try((json.get \ "simulationId").get.as[Int])
    simulationId.isSuccess match{
      case true =>
        ComponentQueue.updateSimulationId(simulationId.get)
        schedulingThread.startExecution()
        Logger.info("Schedule thread created")
        Ok("Starting the scheduler")
      case _=> Ok("simulation Id is not found in Json")
    }
  }

  def startAgain(id:Int)=Action.async { implicit request =>
    val json =request.body.asJson
    val simulationId = Try(id)//Try((json.get \ "simulationId").get.as[Int])
    simulationId.isSuccess match{
      case true =>
        ComponentQueue.updateSimulationId(simulationId.get)
        db.clearPreviousSimulationProcessingDetails(simulationId.get) map{
          case true => schedulingThread.startExecution()
            Logger.info("Schedule thread created")
            Ok("Starting the scheduler")
          case false =>
            Logger.info("Start Again Scheduler thread failed")
            Ok("No Previous scheduling information Found, Can be started normally")
        }

      case _=> Future.successful(Ok("simulation Id is not found in Json"))
    }
  }

  def stop(id:Int) = Action {
        Logger.info("Stop request recieved..")
        if(schedulingThread !=null){
          schedulingThread.endExecution()
          Logger.info("Stop request processed.. ")
        }
        Ok("Thread Stopped")
      }
}
