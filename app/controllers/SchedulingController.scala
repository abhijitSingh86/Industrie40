package controllers

import java.util.Calendar

import actor.FailureActor.{SetSimulation, Start, Stop}
import actor.FailureGeneratorActor
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import data.OnlineData
import db.DbModule
import json.DefaultRequestFormat
import network.NetworkProxy
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import scheduler.{ApplicationLevelData, ComponentQueue, SchedulerThread}

import scala.util.Try

/**
  * Created by billa on 25.04.17.
  */
class SchedulingController(schedulingThread:SchedulerThread,db:DbModule , networkProxy:NetworkProxy)  extends Controller{

  implicit val system = ActorSystem("Assembly-System")
  implicit val materializer = ActorMaterializer()
  lazy val failureGeneratorActor = system.actorOf(Props(new FailureGeneratorActor(networkProxy,db)) , name="failureActor")
  def start(id:Int,versionId:Int)=Action{ implicit request =>
    val simulationId = Try(id)//Try((json.get \ "simulationId").get.as[Int])
    simulationId.isSuccess match{
      case true =>
        failureGeneratorActor ! SetSimulation(simulationId.get)
        ComponentQueue.updateSimulationId(simulationId.get,versionId)
        //Fetch Transport Time Details
        ComponentQueue.setComponentTT(db.getComponentTimeMap(simulationId.get))
        ComponentQueue.setAssemblyTT(db.getAssemblyTimeMap(simulationId.get))
        sendStartMsgToAllComponent(simulationId.get)
        //Start the failure actor
        failureGeneratorActor ! Start
        schedulingThread.startExecution()
        db.updateSimulationStartTime(simulationId.get)
        Logger.info("Schedule thread created")
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      case _=> Ok(DefaultRequestFormat.getValidationErrorResponse(List(("error","simulation Id is not found in Json"))) )
    }
  }

  def sendStartMsgToAllComponent(simulationId:Int)={
    db.getAllComponentUrlBySimulationId(simulationId).map(x=>{
      networkProxy.sendSimulationStartDetails(x._2)
    })
  }

  def checkLoading() = Action{
    val flag = OnlineData.isAllLoaded()
    Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("isLoadingComplete"-> flag )))
  }


  def stop(id:Int,mode:String) = Action {
        Logger.info("Stop request recieved..")
        if(schedulingThread !=null && mode != "view"){
          schedulingThread.endExecution()
          db.updateSimulationEndTime(id)
          ComponentQueue.popAll()
          failureGeneratorActor ! Stop
          db.getAllAssemblyUrlBySimulationId(ComponentQueue.getSimulationId()).map(x=>{
            networkProxy.sendFinishNotificationToAssembly(x._2)
          })

          networkProxy.sendStopToGhostApp()
          Logger.info("Stop request processed.. ")
        }
      val simObj = db.getSimulationObject(id)

      Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("sttime"->simObj.startTime,"ettime"->simObj.endTime)))
  }


  def ghostPing(url:String,port:Int) = Action {
    ApplicationLevelData.ghostSyncTime = Calendar.getInstance().getTimeInMillis
    ApplicationLevelData.ghostUrl = "http://"+url+":"+port
    Ok("pong")
  }
}
