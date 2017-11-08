package utils

import java.sql.Timestamp
import java.util.Calendar

/**
  * Created by billa on 15.05.17.
  */
object DateTimeUtils {

  def getCurrentTimeStamp():Timestamp ={
    new Timestamp(Calendar.getInstance().getTimeInMillis)
  }

  def getOldBySecondsTS(seconds:Int):Timestamp ={
    val c  = Calendar.getInstance()
    c.add(Calendar.SECOND,-seconds)
    val s= new Timestamp(c.getTimeInMillis)
    s
  }
}
