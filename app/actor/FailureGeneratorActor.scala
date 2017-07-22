package actor

import actor.FailureActor.{IntroduceFailure, Start, Stop}
import akka.actor.{Actor, PoisonPill}
import db.DbModule
import models.Assembly

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
}

class FailureGeneratorActor(simulationId:Int,db:DbModule) extends Actor{

  var timesDone=0;
  var failureTimeDone = 0;

  var list:List[Assembly] = List.empty
  var isStopReceived = false

  override def receive: Receive = {

    case Start => {
      list = db.getAllAssembliesForSimulation(simulationId)
      import scala.concurrent.duration._
      context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure)

    }
    case Stop =>{
      isStopReceived=true
    }
    case IntroduceFailure=>{
      if(!isStopReceived){

        val random = new Random(list.length+2)
        val index = random.nextInt();
        if(index < list.length && list(index).fcount > 0 ){
          val assembly = list(index)
          val tempR = new Random(assembly.ftime)
          val failureTime = if(assembly.fcount == 0) assembly.ftime else tempR.nextInt()

          val updatedAssembly = assembly.copy(fcount = assembly.fcount-1,ftime = failureTime)
          list = updatedAssembly :: list.filterNot(_.id == assembly.id)
          //Communicate to assembly for Failure Introduction
          //Store Fail assembly obj in Session to be used by scheduler

          context.system.scheduler.scheduleOnce(failureTime seconds, self, IntroduceFailure)
        }else{
          context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure)
        }
      }else{
        self ! PoisonPill
      }

    }
  }
}
