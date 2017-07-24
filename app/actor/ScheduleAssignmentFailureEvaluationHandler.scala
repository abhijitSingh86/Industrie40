package actor

import models.{Assembly, Component, Operation}
import scheduler.SchedulerAssignmentHandler

class ScheduleAssignmentFailureEvaluationHandler extends SchedulerAssignmentHandler{

  var assemblyFailureCommunication:AssemblyFailureCommunication = null

  def setFailureCommunicationHandler(assemblyFailureCommunication: AssemblyFailureCommunication) = {
    this.assemblyFailureCommunication = assemblyFailureCommunication
  }


  var estimatedFinishTime = 0.0

  def setEFT(time:Long) = {
    estimatedFinishTime = time/1000
  }
  override def assign(component: Component, operation: Operation, assembly: Assembly): Boolean = {

    val newTime = assembly.totalOperations.filter(_.operation.id==operation.id)(0).time

    val randomTransportTime = 5
    if((newTime+randomTransportTime) > estimatedFinishTime*2){
      // wait
      assemblyFailureCommunication.waitCall()
    } else{
      //fail
      assemblyFailureCommunication.failCall()
    }

    true
  }


}
