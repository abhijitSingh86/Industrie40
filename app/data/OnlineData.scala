package data

import java.util.Calendar

import scala.collection.mutable

object OnlineData {

  private var onlineMap = mutable.HashMap[String,Long]()

  private var totalCount = -1

  def isAllLoaded(): Boolean ={
    totalCount == onlineMap.size
  }

  def setTotalComponentCount(count:Int): Unit ={
    totalCount = count
    onlineMap  = mutable.HashMap[String,Long]()
  }

  def updateComponentOnlineState(id:Int): Unit ={
      update(s"c${id}")
  }

  def updateAssemblyOnlineState(id:Int): Unit ={
      update(s"a${id}")
  }

  private def update(key:String): Unit ={
    onlineMap +=  key -> Calendar.getInstance().getTimeInMillis
  }

}
