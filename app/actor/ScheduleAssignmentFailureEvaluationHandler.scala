package actor

import db.DbModule
import models.{Assembly, Component, Operation, OperationProcessingInfo}
import network.NetworkProxy
import play.api.Logger
import scheduler.{ComponentQueue, ScheduleAssignmentDbHandler, SchedulerAssignmentHandler}

class ScheduleAssignmentFailureEvaluationHandler(networkProxy:NetworkProxy,db:DbModule) extends SchedulerAssignmentHandler{

  val  logger = Logger("access")
  var assemblyFailureCommunication:AssemblyFailureCommunication = null

  def setFailureCommunicationHandler(assemblyFailureCommunication: AssemblyFailureCommunication) = {
    this.assemblyFailureCommunication = assemblyFailureCommunication
  }

  var componentPreviousOperation:OperationProcessingInfo=null

  def setComponentPreviousOperation(currentProcessing : OperationProcessingInfo) = {
    this.componentPreviousOperation = currentProcessing
  }




  var estimatedFinishTime = 0.0

  def setEFT(time:Long) = {
    estimatedFinishTime = time/1000
  }
  override def assign(component: Component, operation: Operation, assembly: Assembly): Boolean = {

    logger.info("Schedule Assignment Failure Evaluation handler assignment call started")

    val newTime = assembly.totalOperations.filter(_.operation.id==operation.id)(0).time

    val transportTime = ComponentQueue.getTransportTime(component,assembly)
    if((newTime+transportTime) > estimatedFinishTime + assemblyFailureCommunication.getFailTime() ){
      // wait
      logger.info("Schedule Assignment Failure Evaluation handler assignment wait call started")
      assemblyFailureCommunication.waitCall()
    } else{
      //fail
      //First insert the fail in processing info
      //Get the ScheduleAssignmentDbHandler object and do the database entries
      //

      logger.info("Schedule Assignment Failure Evaluation handler error started")

      db.updateComponentProcessingInfoInFailureScenarion(ComponentQueue.getSimulationId(),ComponentQueue.getSimulationVersionId() ,component.id,componentPreviousOperation.assemblyId,
        component.componentSchedulingInfo.sequence,componentPreviousOperation.operationId)
      logger.info("Schedule Assignment Failure Evaluation handler error failure Db details inserted")

      val scheduleDbHanlder = new ScheduleAssignmentDbHandler(db)
      scheduleDbHanlder.assign(component,operation,assembly)
      logger.info("Schedule Assignment Failure Evaluation handler error new Schedule Db data enttered")

      sendNewScheduleInformationToComponent(ComponentQueue.getSimulationId() , List(component))
      assemblyFailureCommunication.failCall()
      logger.info("Schedule Assignment Failure Evaluation handler error new assembly info sent to component")

    }

    true
  }

  def sendNewScheduleInformationToComponent(simulationId: Int, components: List[Component]) = {
    val updatedCmps = components.map(x=> db.getComponentWithProcessingInfo(x.id,simulationId,ComponentQueue.getSimulationVersionId())).flatten
    val urls = db.getAllComponentUrlBySimulationId(simulationId).toMap
    val assemblyUrls =db.getAllAssemblyUrlBySimulationId(simulationId).toMap

    val assemblies = db.getAllAssembliesForSimulation(simulationId)
    val assemMap = assemblies.map(x=>(x.id -> x)).toMap
    updatedCmps.map(x => {
      //TODO
      // send request at component attached urls for assembly assignments
      val ass = assemMap.get(x.componentSchedulingInfo.currentProcessing.get.assemblyId).get
      //Calculate Transport Time
      val transportTime  = ComponentQueue.getTransportTime(x,ass)

//      logger.info("**********Transport Time is "+transportTime)
      networkProxy.sendNewAssemblyDetailsInFailure(urls.get(x.id).get,ass,assemblyUrls,x.componentSchedulingInfo.currentProcessing.get.operationId , transportTime)
    })
  }


}
