package scheduler
import models.{Assembly, Component, Operation}

import scala.annotation.tailrec
import scala.collection.immutable.{HashMap, ListMap}
import scala.collection.mutable

/**
  * Created by billa on 2016-12-16.
  */
class ComponentScheduler extends Scheduler {
  /**
    * Function will take the component and assemblies and schedule them with Interval timing greedy algorithm.
    * Return will be the list of components which are not scheduled by the algorithm this is possible in case of
    * unavailable resources.
    *
    * @param components
    * @param assemblies
    * @return
    */
  override def scheduleComponents(components: List[Component], assemblies: List[Assembly]): List[Component] = {
    //first get the available resource map
    val availableResourceMap = getAvailableResourceMap(assemblies)
    val requiredOperationMap = ListMap(getRequiredOperationMap(components).toSeq.sortWith(_._2.size < _._2.size):_*)
    val list = List[Option[Component]](None)
    requiredOperationMap.map {
      case (operation, componentList) => {
        componentList.map(component => {
          if (availableResourceMap.contains(operation) && availableResourceMap.get(operation).get.size > 0 ) {

            component.getCurrentOperation() match {
              case None => {
                val assembly = availableResourceMap.get(operation).get(0)
                assembly.allocateOperation(operation)
                component.scheduleCurrentOperation(operation, assembly)
                availableResourceMap + (operation -> (availableResourceMap.get(operation).drop(1)))

              }
              case Some(operation) =>{
                //Component Already scheduled, no action needed
              }
            }
          }else{
            Some(component) :: list
          }
        })

      }
      }
    list.flatten
  }


  def getRequiredOperationMap(components: List[Component]):Map[Operation, List[Component]] = {

    val requiredOperationMap = new mutable.HashMap[Operation, List[Component]]()

    @tailrec
    def processComponents(opMap: mutable.HashMap[Operation, List[Component]], count: Int): Unit = {
      if (count < components.size) {
        //retrieve the assembly's total operation
        val component = components(count)
        component.getCurrentProcessingStepOptions().map(x => {
          //if the operation is not in allocated operation, put it into a map for scheduling
              opMap.contains(x) match {
                case true => opMap += (x -> (opMap.get(x).get :+ component))
                case false => opMap += (x -> List(component))
              }
        })
        processComponents(opMap, count + 1)
      }
    }

    //start the tail recursion
    processComponents(requiredOperationMap, 0)
    //return the operation map
    requiredOperationMap.toMap[Operation,List[Component]]
  }


  def getAvailableResourceMap(assemblies: List[Assembly]) = {

    val availableOperationMap = new mutable.HashMap[Operation, List[Assembly]]()

    @tailrec
    def processAssemblies(opMap: mutable.HashMap[Operation, List[Assembly]], count: Int): Unit = {
      if (count < assemblies.size) {
        //retrieve the assembly's total operation
        val assembly = assemblies(count)
        assembly.totalOperations.map(x => {
          //if the operation is not in allocated operation, put it into a map for scheduling
          assembly.allocatedOperations.contains(x) match {
            case false => {
              opMap.contains(x) match {
                case true => opMap += (x -> (opMap.get(x).get :+ assembly))
                case false => opMap += (x -> List(assembly))
              }
            }
            case true => None
          }
        })
        processAssemblies(opMap, count + 1)
      }
    }

    //start the tail recursion
    processAssemblies(availableOperationMap, 0)
    //return the operation map
    availableOperationMap
  }
}