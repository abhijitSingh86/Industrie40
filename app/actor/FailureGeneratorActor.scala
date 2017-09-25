package actor

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

case object FailureActor{
  case object Start
  case object Stop
  case class IntroduceFailure(counter:Int)
  case class SetSimulation(simulationId:Int)
  case object GetFailedAssembly
}


class AssemblyFailureCommunication(networkProxy: NetworkProxy , url:String,failureTime:Int){

  def getFailTime():Int = failureTime

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

  var starttime:Long = 0
  var counter=0
  val scheduleAssignmentDbHandler = new ScheduleAssignmentDbHandler(db)
  val failureEvaluationHandler = new ScheduleAssignmentFailureEvaluationHandler(networkProxy,db)
  val schedular = new ComponentScheduler(failureEvaluationHandler)

  val logger = Logger("access")
  override def receive: Receive = {

    case GetFailedAssembly =>{
      log.info("Get Assembly Name Recieved")
      val ret = if(failedAssembly.isDefined) failedAssembly.get.id else -1
      sender ! ret
    }
    case SetSimulation(x:Int) => {
      log.info("Set simulation Id Called ${x}")
      simulationId = x
    }

    case Start => {
      log.info(s"Start failure actor received previous stop status ${isStopReceived}")
      counter = counter +1
      isStopReceived = false
      list = db.getAllAssembliesForSimulation(simulationId).filter(_.ifFailAllowed)
      assemblyUrlMap = db.getAllAssemblyUrlBySimulationId(simulationId).toMap
      import scala.concurrent.duration._
      context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure(counter))

    }
    case Stop =>{
      log.info(s"Stop message received ${simulationId}")
      counter =0
      isStopReceived=true
    }
    case x:IntroduceFailure=>{
      log.info("Introduce Failure Called ")


      if(ComponentQueue.failedAssemblyId != -1){
       // db.addEndTimeInAssemblyFailureEntry(simulationId,ComponentQueue.failedAssemblyId,starttime)
        ComponentQueue.failedAssemblyId = -1
      }

      if(!isStopReceived && x.counter  == counter){
        counter = counter +1
        log.info("Introduce Failure Called starting the process")

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
          ComponentQueue.failTime=failureTime
          ComponentQueue.failedAssemblyId = assembly.id

          starttime = db.addAssemblyFailureEntry(simulationId,assembly.id,failureTime)

          val assemblies = db.getAllAssembliesForSimulation(ComponentQueue.getSimulationId())
          //fetch the component Object
          val compTup = db.fetchInProgressComponentOnAssembly(assembly.id,ComponentQueue.getSimulationId())
            compTup._1.map(com =>{

            val filteredBusyAssemblies = assemblies.filterNot(x=>{
              x.allocatedOperations.size > 0 || x.id == ComponentQueue.failedAssemblyId
            })

              logger.info("Introduce Failure filtered Assembly")
              logger.info(filteredBusyAssemblies.mkString(","))

              if(filteredBusyAssemblies.size >0){
              failureEvaluationHandler.setEFT(compTup._2)
              failureEvaluationHandler.setFailureCommunicationHandler(assemblyFailureCommunication)
                failureEvaluationHandler.setComponentPreviousOperation(com.getCurrentOperation().get)
                //Dirty hack to decrement the sequence
                val componentSchedule:ComponentSchedulingInfo = com.componentSchedulingInfo.asInstanceOf[ComponentSchedulingInfo]
                val component = com.copy(componentSchedulingInfo = componentSchedule.copy(sequence = componentSchedule.sequence-1 , currentProcessing = None))
              schedular.scheduleComponents(List(component) , filteredBusyAssemblies).size match{
                case 1 => {
                  logger.info("Introduce Failure Scheduling successfull")

                  //Will be handled by FailureEvaluationHandler
                  //resembles the component is scheduled
                }
                case 0 =>{
                  logger.info("Introduce Failure Scheduling not possible wait invoked")

                  //Can't be Schedlued, So Wait is the only option
                  assemblyFailureCommunication.waitCall()
                }
              }
            }else{
                logger.info("Introduce Failure assemblies not possible wait invoked")

                //No Assembly Available Wait is the only option
              assemblyFailureCommunication.waitCall()
            }


            val updatedAssembly = assembly.copy(fcount = assembly.fcount - 1, ftime = failureTime)
            list = updatedAssembly :: list.filterNot(_.id == assembly.id)
            failedAssembly = Some(assembly)
          } )
          logger.info("Introduce Failure Called No COmponent on assembly found ")

          context.system.scheduler.scheduleOnce(failureTime+1 seconds, self, IntroduceFailure(counter))
        }else{
          context.system.scheduler.scheduleOnce(5 seconds, self, IntroduceFailure(counter))
        }
      }

    }
  }

}
