package scheduler

import play.api.Logger
import scheduler.commands.Command

/**
  * Created by billa on 07.01.17.
  */


object SchedulerThread{

  var thread:Thread  = null
  def  startExecution(o:SchedulerThread): Unit ={
    synchronized {
      o.runflag = true
      thread = new Thread(o)
      thread.start()
      Logger.info("****************************************************************************")
    }
  }

  def endExecution(o:SchedulerThread): Unit ={
    synchronized {
      if (thread != null) {
        o.runflag = false
        //  thread.join()
      }
    }
  }
}

class SchedulerThread(sleepTime:Int,command:Command)  extends Runnable{

  val logger = Logger(this.getClass())

  private  var runflag = false
//  private var thread:Thread = null



  override def run(): Unit = {
    logger.info("Scheduler thread started")
    while(runflag){
      logger.info("Scheduler thread run method started")
      //Sleep for a while before scheduling
      try{
        logger.info("Scheduler thread run method in sleep call")
        Thread.sleep(sleepTime)
        logger.info("Scheduler thread run method after sleep call")
      }
      catch{
        case ex:Exception => {
          logger.error("Exception while sleeping",ex)
          print("error occurred while Sleeping thread")
        }
      }
      logger.info("Scheduler thread is invoking the command")
      command.execute()
      logger.info("Scheduler thread is finished invoking the command")
    }
  }
}
