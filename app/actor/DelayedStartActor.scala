package actor

import actor.DelayedStartActor.{Schedule, send}
import actor.FailureActor._
import akka.actor.{Actor, ActorLogging, PoisonPill}
import db.DbModule
import models.{Assembly, ComponentSchedulingInfo}
import network.NetworkProxy
import play.api.Logger
import scheduler.{ComponentQueue, ComponentScheduler, ScheduleAssignmentDbHandler}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

/**
  * Created by billa on 17.07.17.
  */



case object DelayedStartActor{
  case class Schedule(url:String)
  case class send(url:String)
}

class DelayedStartActor(networkProxy:NetworkProxy) extends Actor with ActorLogging{

  val logger = Logger("access")
  override def receive: Receive = {

    case send(url:String) =>{
      networkProxy.sendSimulationStartDetails(url)
    }

    case Schedule(url:String) =>{
          context.system.scheduler.scheduleOnce(1 seconds, self, send(url))
      }

  }

}
