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
        OnlineData.setStarted(true)
        failureGeneratorActor ! SetSimulation(simulationId.get)
        ComponentQueue.updateSimulationId(simulationId.get,versionId)
        //Fetch Transport Time Details
        ComponentQueue.setComponentTT(db.getComponentTimeMap(simulationId.get))
        ComponentQueue.setAssemblyTT(db.getAssemblyTimeMap(simulationId.get))
        sendStartMsgToAllComponent(simulationId.get)
        //Start the failure actor
        failureGeneratorActor ! Start
        schedulingThread.startExecution()

          //REMOVED not needed
       // db.updateSimulationStartTime(simulationId.get)
        Logger.info("Schedule thread created")
        Ok(DefaultRequestFormat.getEmptySuccessResponse())
      case _=> Ok(DefaultRequestFormat.getValidationErrorResponse(List(("error","simulation Id is not found in Json"))) )
    }
  }

  /**
    * Function to send the start scheduling to all online components also with others ut will send immediate scheduling
    * start in case of already scheduled components
    * @param simulationId
    * @return
    */
  def sendStartMsgToAllComponent(simulationId:Int)={
    db.getAllComponentUrlBySimulationId(simulationId).filter(y=>OnlineData.isComponentOnline(y._1)).map(x=>{
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
          OnlineData.setStarted(false)
          schedulingThread.endExecution()
          //REMOVED not needed
          //db.updateSimulationEndTime(id)
          ComponentQueue.popAll()
          OnlineData.resetOnlineData()
          failureGeneratorActor ! Stop
          //REMOVED as ghost app will handle this
//          db.getAllAssemblyUrlBySimulationId(ComponentQueue.getSimulationId()).map(x=>{
//            networkProxy.sendFinishNotificationToAssembly(x._2)
//          })

          networkProxy.sendStopToGhostApp()
          Logger.info("Stop request processed.. ")
        }

      val simObj = db.getSimulationTimeDetails(id,ComponentQueue.getSimulationVersionId())

      Ok(DefaultRequestFormat.getSuccessResponse(Json.obj("sttime"->simObj._1,"ettime"->simObj._2)))
  }


  def ghostPing(url:String,port:Int) = Action {
    ApplicationLevelData.ghostSyncTime = Calendar.getInstance().getTimeInMillis
    ApplicationLevelData.ghostUrl = "http://"+url+":"+port
    Ok("pong")
  }
}
