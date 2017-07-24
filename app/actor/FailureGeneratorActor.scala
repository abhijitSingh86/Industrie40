package actor

import actor.FailureActor._
import akka.actor.{Actor, ActorLogging, PoisonPill}
import db.DbModule
import models.Assembly
import network.NetworkProxy
import scheduler.{ComponentQueue, ComponentScheduler, ScheduleAssignmentDbHandler}

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


class AssemblyFailureCommunication(networkProxy: NetworkProxy , url:String,failureTime:Int){

  def failCall() = sendFailureInfoToAssembly(url,failureTime,"error")

  def waitCall() = sendFailureInfoToAssembly(url,failureTime,"wait")

  private def sendFailureInfoToAssembly(url:String,time:Int,action:String): Unit ={
    networkProxy.sendFailureNotificationToAssembly(url,time,action)
  }

}

class FailureGeneratorActor(networkProxy:NetworkProxy,db:DbModule) extends Actor with ActorLogging{

  var timesDone=0;
  var failureTimeDone = 0;
  var simulationId = -1
  var list:List[Assembly] = List.empty
  var assemblyUrlMap:Map[Int,String] = Map.empty
  var isStopReceived = false
  var failedAssembly:Option[Assembly] =None

  val scheduleAssignmentDbHandler = new ScheduleAssignmentDbHandler(db)
  val failureEvaluationHandler = new ScheduleAssignmentFailureEvaluationHandler()
  val schedular = new ComponentScheduler(failureEvaluationHandler)

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
          val assemblyUrl = assemblyUrlMap.get(assembly.id).get
          val failureTime = if(assembly.fcount == 1) assembly.ftime else random.nextInt(assembly.ftime)

          val assemblyFailureCommunication = new AssemblyFailureCommunication(networkProxy,assemblyUrl,failureTime)

          //Decide component action
          val componentAction = "error"
//          val componentAction = "wait"
          ComponentQueue.failedAssemblyId = assembly.id
          val assemblies = db.getAllAssembliesForSimulation(ComponentQueue.getSimulationId())
          //fetch the component Object
          val compTup = db.fetchInProgressComponentOnAssembly(assembly.id,ComponentQueue.getSimulationId())
            compTup._1.map(com =>{

            val filteredBusyAssemblies = assemblies.filterNot(x=>{
              x.allocatedOperations.size > 0 || x.id == ComponentQueue.failedAssemblyId
            })

            if(filteredBusyAssemblies.size >0){
              failureEvaluationHandler.setEFT(compTup._2)
              failureEvaluationHandler.setFailureCommunicationHandler(assemblyFailureCommunication)
              schedular.scheduleComponents(List(com) , filteredBusyAssemblies).size match{
                case 1 => {
                  //Will be handled by FailureEvaluationHandler
                }
                case 0 =>{
                  //Wait is the only option
                  assemblyFailureCommunication.waitCall()
                }
              }
            }else{
              //No Assembly Available Wait is the only option
              assemblyFailureCommunication.waitCall()
            }


            val updatedAssembly = assembly.copy(fcount = assembly.fcount - 1, ftime = failureTime)
            list = updatedAssembly :: list.filterNot(_.id == assembly.id)
            failedAssembly = Some(assembly)
          } )

          context.system.scheduler.scheduleOnce(failureTime+1 seconds, self, IntroduceFailure)
        }else{
          context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure)
        }
      }

    }
  }

}
