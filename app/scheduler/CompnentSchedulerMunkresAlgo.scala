package scheduler
import models.{Assembly, Component, Operation}
import scheduler.algo.MunkresAlgorithmImpl

import scala.annotation.tailrec
import scala.collection.immutable.ListMap
import scala.collection.mutable

class CompnentSchedulerMunkresAlgo(scheduleDbHandler:SchedulerAssignmentHandler)  extends Scheduler{
  val NA_VALUE:Int = 99998

  /**
    * Function will take the component and assemblies and schedule them with Interval timing greedy algorithm.
    * Return will be the list of components which are not scheduled by the algorithm this is possible in case of
    * unavailable resources.
    *
    * @param components
    * @param assemblies
    * @return Scheduled Components Ids
    */
  override def scheduleComponents(components: List[Component], assemblies: List[Assembly]):List[Int] = {
    //create cost Matrix
    val unBalancedCostMatrix = assemblies.map(as=>{

      components.map(cmp=>{
        val processings = cmp.getCurrentProcessingStepOptions().distinct.map(_.id)
        if(as.totalOperations.filter(op => processings.contains(op.operation.id)).size >0){
          ComponentQueue.getTransportTime(cmp,as)
        }else
          NA_VALUE
              }).toArray

    }).toArray

    //Check exception condition where no requirements can be fulfilled
    if(unBalancedCostMatrix.flatten.filterNot(_ == NA_VALUE).size ==0){
      //Returning as no requirement is there to fulfill
      return List()
    }

    //send to Munkres Impl class for calculation
  val algoObj =new  MunkresAlgorithmImpl(unBalancedCostMatrix)
    val assignmentMatrix:Array[Array[Int]] = algoObj.getAssignmentmatrix
    //assign the scheduling if any
    val scheduledComponent = mutable.ArrayBuffer[Int]()

    assignmentMatrix.zipWithIndex.foreach{case (ele,index) =>{
        ele.zipWithIndex.foreach{
          case (c,ind) => {
            if(c==1){
              val component:Component =  components(ind)
              val assembly:Assembly = assemblies(index)
              val processings = component.getCurrentProcessingStepOptions().distinct
              val operation:Operation = assembly.totalOperations.filter(op=> processings.contains(op.operation))(0).operation
              scheduleDbHandler.assign(component,operation,assembly)
              scheduledComponent += component.id
            }
          }
        }
    }}

    //return the assined list
    scheduledComponent.toList
  }


}
