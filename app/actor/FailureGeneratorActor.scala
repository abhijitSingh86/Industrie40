package actor

import actor.FailureActor._
import akka.actor.{Actor, ActorLogging, PoisonPill}
import db.DbModule
import models.Assembly
import network.NetworkProxy
import scheduler.ComponentQueue

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

/**
  * Created by billa on 17.07.17.
  */

case object FailureActor{
  case object Start
  case object Stop
  case object IntroduceFailure
  case class SetSimulation(simulationId:Int)
  case object GetFailedAssembly
}

class FailureGeneratorActor(networkProxy:NetworkProxy,db:DbModule) extends Actor with ActorLogging{

  var timesDone=0;
  var failureTimeDone = 0;
  var simulationId = -1
  var list:List[Assembly] = List.empty
  var assemblyUrlMap:Map[Int,String] = Map.empty
  var isStopReceived = false
  var failedAssembly:Option[Assembly] =None

  override def receive: Receive = {

    case GetFailedAssembly =>{
      println("Get Assembly Name Recieved")
      val ret = if(failedAssembly.isDefined) failedAssembly.get.id else -1
      sender ! ret
    }
    case SetSimulation(x:Int) => {
      simulationId = x
    }

    case Start => {
      isStopReceived = false
      list = db.getAllAssembliesForSimulation(simulationId)
      assemblyUrlMap = db.getAllAssemblyUrlBySimulationId(simulationId).toMap
      import scala.concurrent.duration._
      context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure)

    }
    case Stop =>{
      isStopReceived=true
    }
    case IntroduceFailure=>{
      if(!isStopReceived){

        val random = new Random()
        val index = random.nextInt(list.length*2);
        if(index < list.length && list(index).fcount > 0 ){
          val assembly = list(index)
          val failureTime = if(assembly.fcount == 1) assembly.ftime else random.nextInt(assembly.ftime)
          //Decide component action
          val componentAction = "error"
          ComponentQueue.failedAssemblyId = assembly.id
          //Communicate to assembly for Failure Introduction
          val flag:Boolean = networkProxy.sendFailureNotificationToAssembly(assemblyUrlMap.get(assembly.id).get,failureTime,componentAction)
//          if(flag) {
            val updatedAssembly = assembly.copy(fcount = assembly.fcount - 1, ftime = failureTime)
            list = updatedAssembly :: list.filterNot(_.id == assembly.id)
            failedAssembly = Some(assembly)
//          }
          //Store Fail assembly obj in Session to be used by scheduler
          context.system.scheduler.scheduleOnce(failureTime+1 seconds, self, IntroduceFailure)
        }else{
          context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure)
        }
      }

    }
  }
}
