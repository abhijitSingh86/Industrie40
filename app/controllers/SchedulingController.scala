package controllers

import db.DbModule
import json.DefaultRequestFormat
import play.api.Logger
import play.api.mvc.{Action, Controller}
import scheduler.{ComponentQueue, SchedulerThread}

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
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      case _=> Ok(DefaultRequestFormat.getValidationErrorResponse(List(("error","simulation Id is not found in Json"))) )
    }
  }



  def stop(id:Int) = Action {
        Logger.info("Stop request recieved..")
        if(schedulingThread !=null){
          schedulingThread.endExecution()
          Logger.info("Stop request processed.. ")
        }
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      }
}
