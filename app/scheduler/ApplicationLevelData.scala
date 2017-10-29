package scheduler

import java.util.Calendar

object ApplicationLevelData {

  def isGhostOnline():Boolean = {
    (Calendar.getInstance().getTimeInMillis -  ghostSyncTime ) < 1200
  }
  var ghostSyncTime:Long =0l
  var ghostUrl:String=""
}
