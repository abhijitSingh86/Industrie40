package scheduler
import models.{Assembly, Component, Operation}
import play.api.Logger

import scala.annotation.tailrec
import scala.collection.immutable.{HashMap, ListMap}
import scala.collection.mutable

/**
  * Created by billa on 2016-12-16.
  */
class ComponentScheduler(scheduleDbHandler:SchedulerAssignmentHandler) extends Scheduler {


  private def getAvailableResourceMapString(map:Map[Operation,List[Assembly]]):String ={
    map.map(x=> {
      val st = x._2.map(_.id.toString+",")
      s"opId:${x._1.id} opName${x._1.name} -> ${st}"
    }).mkString(", \n")
  }
  private val logger = Logger("access")
  /**
    * Function will take the component and assemblies and schedule them with Interval timing greedy algorithm.
    * Return will be the list of components which are not scheduled by the algorithm this is possible in case of
    * unavailable resources.
    *
    * @param components
    * @param assemblies
    * @return List of Scheduled Components
    */
  override def scheduleComponents(components: List[Component], assemblies: List[Assembly]): List[Int] = {
    //first get the available resource map
    var availableResourceMap = ListMap(getAvailableResourceMap(assemblies).toSeq.sortWith(_._2.size < _._2.size):_*)
    val requiredOperationMap = ListMap(getRequiredOperationMap(components).toSeq.sortWith(_._2.size < _._2.size):_*)
    logger.info("*************************************************")
    logger.info(getAvailableResourceMapString(availableResourceMap))
    logger.info("--------------------------------------------------")
    requiredOperationMap.foreach(x=>{
      val st = x._2.map(_.id.toString+",")
      logger.info(s"opId:${x._1.id} opName${x._1.name} -> ${st}")
    })
    logger.info("*************************************************")
    //    val list = List[Option[Component]](None)
    val scheduledComponent = mutable.ArrayBuffer[Int]()
     requiredOperationMap.map {
      case (operation, componentList) => {
        componentList.map(component => {
          if (availableResourceMap.contains(operation) && availableResourceMap.get(operation).get.size > 0 ) {

            component.getCurrentOperation() match {
              case None if(!scheduledComponent.contains(component.id)) => {
                val assembly = availableResourceMap.get(operation).get(0)

                scheduleDbHandler.assign(component,operation,assembly)
                availableResourceMap = removeAssemblyFromAvailabelResourceMap(availableResourceMap,assembly)
                scheduledComponent += component.id
              }
              case _ =>{
                logger.info(s"Skipping scheduling for this component ${component.id} flag: ${component.getCurrentOperation()} list: ${scheduledComponent}")
                //Component Already scheduled, no action needed
              }
            }
          }else{
            logger.info(s"Required Operation not available scheduling for this component ${component.id} flag: ${operation} list: ${availableResourceMap.get(operation)} map:${getAvailableResourceMapString(availableResourceMap)}")
          }
        })

      }

      }
//    components.filter(x=> !scheduledComponent.contains(x.id))
    scheduledComponent.toList
  }

  def removeAssemblyFromAvailabelResourceMap(availableResourceMap: ListMap[Operation, List[Assembly]], assembly: Assembly)
  : ListMap[models.Operation, scala.List[models.Assembly]] = {

    val availableOperationMap = new mutable.ListMap[Operation, List[Assembly]]()

    assembly.totalOperations.map(x=>
      if(availableResourceMap.get(x.operation).isDefined){
        val updatedAssemblyList = availableResourceMap.get(x.operation).get.filterNot(_.id == assembly.id)
        availableOperationMap += x.operation -> updatedAssemblyList
      }
    )
    ListMap((availableResourceMap ++ availableOperationMap).toSeq:_*)
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