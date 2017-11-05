package data

import java.util.Calendar

import models.{Component, Simulation}

import scala.collection.mutable

object OnlineData {

  private var simulationData:Option[Simulation] = None

  private var onlineMap = mutable.HashMap[String,Long]()

  private var totalCount = -1

  def getTotalComponents(): List[Component] ={
    if(simulationData.isDefined)
      simulationData.get.components
    else
      List.empty[Component]
  }

  def setSimulationData(sim:Simulation)={
    simulationData = Some(sim)
  }

  def isAllLoaded(): Boolean ={
    totalCount == onlineMap.size
  }

  def resetOnlineData():Unit = {
    onlineMap = mutable.HashMap[String,Long]()
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
