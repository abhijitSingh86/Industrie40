package data

import java.util.Calendar

import models.{Component, Simulation}

import scala.collection.mutable

object OnlineData {

  private var simulationData:Option[Simulation] = None

  private var onlineMap = mutable.HashMap[String,Long]()

  private var totalCount = -1

  private var isSimulationStarted=false

  def isStarted(): Boolean ={
    isSimulationStarted
  }

  def setStarted(b:Boolean)={
    isSimulationStarted = b;
  }

  def getTotalComponents(): List[Component] ={
    if(simulationData.isDefined)
      simulationData.get.components
    else
      List.empty[Component]
  }

  def setSimulationData(sim:Simulation)={
    simulationData = Some(sim)
  }

  def isComponentOnline(id:Int):Boolean = {
    onlineMap.contains(s"c${id}")
  }

  /**
    * this function will start the execution as soon as 10 components are active for execution.
    * This feature will be handy in case of large number of components.Ghost app will start new component when one completes
    * @return
    */
  def isAllLoaded(): Boolean ={
    if(totalCount > 0 && getTotalComponents().size >10){
      onlineMap.size >= 10 + simulationData.get.assemblies.size
    }else
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
