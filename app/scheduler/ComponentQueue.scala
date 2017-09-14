package scheduler

import models.{Assembly, AssemblyTransportTime, Component, ComponentToAssemblyTransTime}
import play.api.Logger

import scala.collection.mutable

/**
  * Created by billa on 07.01.17.
  */
object ComponentQueue {

  private var componentTT:List[ComponentToAssemblyTransTime] =List()
  private var assemvlyTT:List[AssemblyTransportTime] = List()
  val logger = Logger(this.getClass())
  val requestQueue:mutable.LinkedHashSet[Component] = new mutable.LinkedHashSet[Component]()
  private var simulationId =0

  var ghostUrl:String=""


  var failTime=0

  def setComponentTT(l:List[ComponentToAssemblyTransTime])={
    componentTT = l
  }

  def getComponentTT():List[ComponentToAssemblyTransTime] = componentTT

  def getAssemblyTT():List[AssemblyTransportTime] = assemvlyTT

  def setAssemblyTT(l:List[AssemblyTransportTime])= {
    assemvlyTT = l
  }

  def getTransportTime(x:Component,a:Assembly):Int = {

    if(x.componentSchedulingInfo.pastProcessings.size == 0){
      ComponentQueue.getTransportTimeForComponentToAssembly(x.id,a.id,simulationId)
    }else{
      val last = x.componentSchedulingInfo.pastProcessings.sortWith(_.endTime > _.endTime).head
      ComponentQueue.getTransportTimeForAssemblyToAssembly(last.assemblyId,a.id,simulationId)
    }
  }

  private def getTransportTimeForAssemblyToAssembly(assemblyId: Int, assemblyId2: Int, simulationId: Int):Int = {
    val assemTTs = assemvlyTT
    val compTT = assemTTs.filter(x=> {
      (x.assembly1 == assemblyId && x.assembly2 == assemblyId2) || (x.assembly1 == assemblyId2 && x.assembly2 == assemblyId)
    })
    if(compTT.isEmpty)
      0
    else
      compTT(0).transportTime
  }

  private def getTransportTimeForComponentToAssembly(componentId: Int, assemblyId: Int, simulationId: Int):Int = {
    val compTTs = componentTT
    val compTT = compTTs.filter(_.component ==componentId)
    if(compTT.isEmpty)
      0
    else
      compTT(0).transportTime
  }

  var failedAssemblyId = -1
  def updateSimulationId(id:Int): Unit ={
    simulationId  = id
  }
  def getSimulationId():Int=simulationId

  def push(component:Component): Unit ={
    logger.info("push invoked size"+requestQueue.size)
    requestQueue.synchronized{

      requestQueue +=  component

    }
    logger.info("push finished size"+requestQueue.size)
  }

  def pop():Option[Component]= {
    requestQueue.synchronized {
      requestQueue.size match {
        case x if x > 0 =>
          requestQueue.headOption
        case _ => None
      }
    }
  }

  def popAll(): List[Component] = {
    logger.info("popAll invoked size"+requestQueue.size)
    requestQueue.synchronized {
      requestQueue.size match {
        case x if x > 0 =>
          logger.info("popAll dequeuing size"+requestQueue.size)
          val comps = requestQueue.filter((_) => true).toList
          requestQueue.clear()
          comps
        case _ => {
          logger.info("found empty list")
          List[Component]()
        }
      }
    }
  }

    def size():Int={
      requestQueue.synchronized {
        requestQueue.size
      }
    }
}


