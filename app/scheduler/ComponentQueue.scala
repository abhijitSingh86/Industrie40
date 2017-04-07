package scheduler

import models.Component
import play.api.Logger

import scala.collection.mutable

/**
  * Created by billa on 07.01.17.
  */
object ComponentQueue {

  val logger = Logger(this.getClass())
  val requestQueue:mutable.Queue[Component] = new mutable.Queue[Component]()


  def push(component:Component): Unit ={
    logger.info("push invoked size"+requestQueue.size)
    requestQueue.synchronized{
      requestQueue+=component
    }
    logger.info("push finished size"+requestQueue.size)
  }

  def pop():Option[Component]= {
    requestQueue.synchronized {
      requestQueue.size match {
        case x if x > 0 =>
          Some(requestQueue.dequeue())
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
          requestQueue.dequeueAll((_) => true).toList
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


