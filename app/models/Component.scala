package models

import enums.PriorityEnum.PriorityEnum
import enums.StateEnum.StateEnum
import enums.{PriorityEnum, StateEnum}

import scala.collection.mutable

/**
  * Created by billa on 2016-12-15.
  */
case class Component(id: Long, name: String, priority: PriorityEnum, processingSequences: List[ProcessingSequence]) {
  private var currentStep: Int = 0
  private var currentState: StateEnum = StateEnum.WAITING
  private var currentAllocatedAssembly: Option[Assembly] = None

  private val completedOperations: mutable.MutableList[Operation] = mutable.MutableList()
  private var currentOperation: Option[Operation] = None

  private def updateCurrentOperation(operation: Option[Operation]): Unit = {
    currentOperation = operation
  }

  def getCurrentAllocatedAssembly(): Option[Assembly] = currentAllocatedAssembly

  def getCurrentOperation(): Option[Operation] = currentOperation

  def updateCurrentState(state: StateEnum) = {
    currentState = state
  }


  /**
    * Function to allocate the assembly and current operation. This should be invoked by the scheduler after deciding
    * upon the schedule.
    */
  def scheduleCurrentOperation(operation: Operation, assembly: Assembly): Unit = {
    this.currentOperation = Some(operation)
    this.currentAllocatedAssembly = Some(assembly)
  }

  /**
    * Function to mark complete the current operation. This function should be called by assembly once it finishes the
    * scheduling.
    */
  def completeCurrentStep() = {
    currentOperation match {
      case Some(x) => {
        completedOperations += x
        incrementCurrentStepCount()
        updateCurrentOperation(None)
      }
      case None => None //throw new Exception("Nothing to complete, No Operation was assigned")
    }
    currentOperation
  }

  private def incrementCurrentStepCount() = {
    currentStep += 1
  }

  def getCurrentProcessingStepOptions(): Seq[Operation] = {
    processingSequences.map(x => {
      completedOperations == x.seq.take(currentStep) match {
        case true => Some(x.seq(currentStep))
        case _ => None
      }
    }).flatten
  }

}
