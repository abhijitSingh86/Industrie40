package scheduler

import models.Component
import play.api.Logger

import scala.collection.mutable

/**
  * Created by billa on 07.01.17.
  */
object ComponentQueue {

  val logger = Logger(this.getClass())
  val requestQueue:mutable.LinkedHashSet[Component] = new mutable.LinkedHashSet[Component]()
  private var simulationId =0

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


