package scheduler

import scheduler.commands.Command
import utils.LoggingUtil._

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
      fileLog.info("****************************************************************************")
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


  private  var runflag = false
//  private var thread:Thread = null



  override def run(): Unit = {
    fileLog.info("Scheduler thread started")
    while(runflag){
      fileLog.info("Scheduler thread run method started")
      //Sleep for a while before scheduling
      try{
        fileLog.info("Scheduler thread run method in sleep call")
        Thread.sleep(sleepTime)
        fileLog.info("Scheduler thread run method after sleep call")
      }
      catch{
        case ex:Exception => {
          fileLog.error("Exception while sleeping",ex)
          print("error occurred while Sleeping thread")
        }
      }
      fileLog.info("Scheduler thread is invoking the command")
      command.execute()
      fileLog.info("Scheduler thread is finished invoking the command")
    }
  }
}
