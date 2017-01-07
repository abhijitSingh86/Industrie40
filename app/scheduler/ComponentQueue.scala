package scheduler

import models.Component

import scala.collection.mutable

/**
  * Created by billa on 07.01.17.
  */
object ComponentQueue {

  val requestQueue:mutable.Queue[Component] = new mutable.Queue[Component]()


  def push(component:Component): Unit ={
    requestQueue.synchronized{
      requestQueue+=component
    }
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
      requestQueue.synchronized {
        requestQueue.size match {
          case x if x > 0 =>
            requestQueue.dequeueAll((x) => true).toList
          case _ => List[Component]()
        }
      }
    }

    def size():Int={
      requestQueue.synchronized {
        requestQueue.size
      }
    }
}


