package scheduler

import java.util.concurrent.CountDownLatch

/**
  * Created by billa on 07.01.17.
  */
class SchedulerThread  extends Runnable{

  val sleepTime = 5000
  override def run(): Unit = {
    while(true){
      //Sleep for a while before scheduling
      try{
        Thread.sleep(sleepTime)
      }
      catch{
        case ex:Exception => {
          print("error occurred ")
        }
      }

      //retrieve all the assemblies to schedule on

      //retrieve all components from the queue

      //call algorithm for scheduling

      //add pending if any to the component queue again




    }




  }
}
