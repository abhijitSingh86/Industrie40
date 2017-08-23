package scheduler
import models.{Assembly, Component, Operation}
import scheduler.algo.MunkresAlgorithmImpl

import scala.annotation.tailrec
import scala.collection.immutable.ListMap
import scala.collection.mutable

class CompnentSchedulerMunkresAlgo(scheduleDbHandler:SchedulerAssignmentHandler)  extends Scheduler{
  /**
    * Function will take the component and assemblies and schedule them with Interval timing greedy algorithm.
    * Return will be the list of components which are not scheduled by the algorithm this is possible in case of
    * unavailable resources.
    *
    * @param components
    * @param assemblies
    * @return Scheduled Components Ids
    */
  override def scheduleComponents(components: List[Component], assemblies: List[Assembly]) = {
    //create cost Matrix
    var availableResourceMap = getAvailableResourceMap(assemblies)
    val unBalancedCostMatrix = components.map(x=>{
      x.getCurrentProcessingStepOptions().distinct.flatMap(y=>{
        assemblies.map(z=>{
          if(z.totalOperations.filter(_.operation.id == y.id).size > 0){
            ComponentQueue.getTransportTime(x,z)
          }else
            99998
        })

      }).toArray
    })



    //send to Munkres Impl class for calculation
  val algoObj =new  MunkresAlgorithmImpl(unBalancedCostMatrix.toArray)
    val assignmentMatrix:Array[Array[Int]] = algoObj.getAssignmentmatrix
    //assign the scheduling if any
    val scheduledComponent = mutable.ArrayBuffer[Int]()
    assignmentMatrix.map(row=>{
      row.map(col=>{
        if(col == 1){
          val component:Component
          val operation:Operation
          val assembly:Assembly
          scheduleDbHandler.assign(component,operation,assembly)
          scheduledComponent += component.id
        }
      })
    })

    //return the assined list

    scheduledComponent.toList
  }

  private def getRequiredOperationMap(components: List[Component]):Map[Operation, List[Component]] = {

    val requiredOperationMap = new mutable.HashMap[Operation, List[Component]]()

    @tailrec
    def processComponents(opMap: mutable.HashMap[Operation, List[Component]], count: Int): Unit = {
      if (count < components.size) {
        //retrieve the assembly's total operation
        val component = components(count)
        if (!component.isComplete()) {
          component.getCurrentProcessingStepOptions().map(x => {
            //if the operation is not in allocated operation, put it into a map for scheduling
            opMap.contains(x) match {
              case true => opMap += (x -> (opMap.get(x).get :+ component))
              case false => opMap += (x -> List(component))
            }
          })
        }
        processComponents(opMap, count + 1)
      }
    }

    //start the tail recursion
    processComponents(requiredOperationMap, 0)
    //return the operation map while make unique list for same operation requirement. Will help to minimize the loops
    requiredOperationMap.map(x=> (x._1 -> x._2.distinct)).toMap[Operation,List[Component]]
  }


  private def getAvailableResourceMap(assemblies: List[Assembly]) :Map[Operation,List[Assembly]]= {

    val availableOperationMap = new mutable.HashMap[Operation, List[Assembly]]()

    @tailrec
    def processAssemblies(opMap: mutable.HashMap[Operation, List[Assembly]], count: Int): Unit = {
      if (count < assemblies.size) {
        //retrieve the assembly's total operation
        val assembly = assemblies(count)
        assembly.totalOperations.map(x=>{
          opMap.contains(x.operation) match {
            case true => opMap += (x.operation -> (opMap.get(x.operation).get :+ assembly))
            case false => opMap += (x.operation -> List(assembly))
          }
        })
        processAssemblies(opMap, count + 1)
      }
    }

    //start the tail recursion
    processAssemblies(availableOperationMap, 0)
    //return the operation map
    availableOperationMap.map(x=>{
      x._1 -> x._2.sortWith(_.totalOperations.size<_.totalOperations.size)
    })toMap
  }
}
