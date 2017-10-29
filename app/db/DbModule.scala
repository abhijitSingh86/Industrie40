package db

import java.util.Calendar

import db.dao._
import db.generatedtable.Tables
import models._
import play.api.cache.CacheApi

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
/**
  * Created by billa on 25.04.17.
  */
trait DbModule {


  def addAssemblyFailureEntry(simulationId:Int,simulationVersionId:Int,assemblyId:Int,duration:Int):Long

  def addEndTimeInAssemblyFailureEntry(simulationId:Int,simulationVersionId:Int,assemblyId:Int,stdate:Long)

  def getAssemblyFailureEntries(simulationId:Int,simulationVersionId:Int):Future[Seq[Tables.AssemblyfailuredataRow]]

  def saveJsoninDatabaseforClone(simulationId:Int,jsondata:String):Unit

  def getJsonFromCloneDatabase(simulationId:Int):String

  def updateComponentProcessingInfoInFailureScenarion(simulationId: Int,simulationVersionId:Int, componentId: Int, assemblyId: Int, sequence: Int, operationId: Int ):Boolean

  def updateSimulationEndTime(simulationId:Int):Boolean


  def getCompleteSimulationObject(simulationId:Int):Simulation

  def getAllSimulation():List[Simulation]

  def getComponentMappedToSimulationId(componentId:Int, simulationId:Int):Option[Component]

  def getAssemblyMappedToSimulationId(assemblyId:Int, simulationId:Int):Option[Assembly]

  def addComponentUrlToSimulationMapEntry(simulationId:Int, componentId:Int,url:String):Boolean

  def addAssemblyUrlToItsMappingEntry(simulationId:Int,assemblyId:Int,url:String):Boolean

  def getAllAssembliesForSimulation(simulationid:Int):List[Assembly]

  def getAllComponentUrlBySimulationId(simulationId:Int):List[(Int,String)]

  def getAllAssemblyUrlBySimulationId(simunlationId:Int):List[(Int,String)]

//  def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit

  def addSimulation(name:String,desc:String):Int

  def addOperation(o:Operation):Int

  def addComponent(c:Component):Int

  def getComponentWithProcessingInfo(componentId:Int,simulationId:Int,simulationVersionId:Int):Option[Component]

  def addComponentsToSimulation(simulationId:Int,componentsId:List[Int])

  def addAssembliesToSimulation(simulationId:Int,assemblyIds:List[Int])

  def addAssembly(a:Assembly):Int

  def fetchInProgressComponentOnAssembly(assemblyId:Int,simulationid:Int,simulationVersionId:Int):(Option[Component],Long)

  def addComponentProcessingInfo(simId:Int,simulationVersionId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int , operationTime:Int):Boolean

  def updateAssemblyOperationStatus(assemblyId:Int, operationId:Int, status:String):Boolean

  def updateComponentProcessingInfo(simulationId: Int,simulationVersionId:Int, componentId: Int, assemblyId: Int, sequence: Int, operationId: Int, failureWaitTime: Int):Boolean

  def incrementSimulationVersionDetails(simulationId:Int):Future[Int]


  def getAssemblyRunningStatus(assemblyId:Int,simulationId:Int ,simulationVersionId:Int):Future[Seq[Tables.ComponentProcessingStateRow]]

  def getComponentNameMapBySimulationId(simulationId:Int):Map[Int,String]

  def getComponentProcessingInfoForSimulation(simulationId: Int , simulationVersionId:Int)
  :Future[Seq[Tables.ComponentProcessingStateRow]]

  def getComponentById(componentId:Int ,simulationId:Int,simulationVersionId:Int, processingRecords:Seq[Tables.ComponentProcessingStateRow]):Component

  def updateSimulationStartTime(simulationId:Int):Boolean

  def addAssemblyTimeMap(simulationId:Int,assemblyTransTime: List[AssemblyTransportTime]):Unit

  def getAssemblyTimeMap(simulationId:Int):List[AssemblyTransportTime]

  def addComponentTimeMap(simulationId:Int,componentToAssemblyTransTime: List[ComponentToAssemblyTransTime]):Unit
  def getComponentTimeMap(simulationId:Int): List[ComponentToAssemblyTransTime]

  def getAssemblyNameMapForSimulation(simulationid:Int):Map[Int,String]
  def getSimulationObject(id:Int):Simulation
}

class SlickModuleImplementation(cache:CacheApi) extends DbModule {

  this: SimulationDaoRepo
    with AssemblyDaoRepo
    with ComponentDaoRepo
    with OperationDaoRepo
    with DBComponent =>


  def addAssemblyFailureEntry(simulationId:Int,simulationVersionId:Int,assemblyId:Int,duration:Int):Long={
    assembly.addAssemblyFailureEntry(simulationId,simulationVersionId:Int,assemblyId,duration)
  }

  def addEndTimeInAssemblyFailureEntry(simulationId:Int,simulationVersionId:Int,assemblyId:Int,stdate:Long) ={
    assembly.addEndTimeInAssemblyFailureEntry(simulationId,simulationVersionId:Int,assemblyId,stdate)
  }

  def getAssemblyFailureEntries(simulationId:Int,simulationVersionId:Int):Future[Seq[Tables.AssemblyfailuredataRow]] = {
    assembly.getAssemblyFailureEntries(simulationId,simulationVersionId)
  }


  def saveJsoninDatabaseforClone(simulationId:Int,jsondata:String): Unit ={
    simulation.saveJsoninDatabaseforClone(simulationId,jsondata)
  }

  def getJsonFromCloneDatabase(simulationId:Int):String ={
    simulation.getJsonFromCloneDatabase(simulationId)
  }

  def getAssemblyNameMapForSimulation(simulationid:Int):Map[Int,String] = {
    assembly.selectAssemblyNameMapBySimulationId(simulationid,cache)
  }

  def getSimulationObject(id:Int):Simulation ={
    simulation.getSimulationById(id)
  }

  def getAssemblyTimeMap(simulationId:Int):List[AssemblyTransportTime] = {
    cache.getOrElse[List[AssemblyTransportTime]](s"assemblyTT${simulationId}") {
      simulation.getAssemblyTimeMap(simulationId)
    }
  }

  def getComponentTimeMap(simulationId:Int): List[ComponentToAssemblyTransTime] = {
    cache.getOrElse[List[ComponentToAssemblyTransTime]](s"componentTT${simulationId}") {
      simulation.getComponentTimeMap(simulationId)
    }
  }

  def addAssemblyTimeMap(simulationId:Int,assemblyTransTime: List[AssemblyTransportTime]):Unit = {
    simulation.addAssemblyTimeMap(simulationId,assemblyTransTime)
  }

  def addComponentTimeMap(simulationId:Int,componentToAssemblyTransTime: List[ComponentToAssemblyTransTime]):Unit = {
    simulation.addComponentTimeMap(simulationId,componentToAssemblyTransTime)
  }

  def fetchInProgressComponentOnAssembly(assemblyId:Int,simulationid:Int,simulationVersionId:Int):(Option[Component],Long) ={
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationid,cache)
    val comps =Await.result(
      component.getComponentProcessingInfoForSimulation(simulationid,simulationVersionId , cache,assemblyNameMap).map(x=>
      x.filter(y=> y.assemblyid == assemblyId && y.status.equalsIgnoreCase(InProgressProcessingStatus.text) && y.simulationid == simulationid))
      , Duration.Inf)

    comps.size match {
      case 1 =>{
        val row = comps(0)
        val etl = (System.currentTimeMillis() - row.startTime)
        ((component.selectByComponentSimulationId(row.componentid,simulationid,simulationVersionId ,cache,assemblyNameMap)) , etl)

      }
      case _ => (None,0)
    }


  }

  def updateSimulationEndTime(simulationId:Int):Boolean = {
    val etTime = component.getLastEndTimeFromComponentProcessingInfo(simulationId)
    simulation.updateEndTime(simulationId , etTime)
  }

  def updateSimulationStartTime(simulationId:Int):Boolean = {
    val stTime = Calendar.getInstance().getTimeInMillis
    simulation.updateStartTime(simulationId , stTime)
  }

  def getComponentById(componentId:Int ,simulationId:Int,simulationVersionId:Int, processingRecords:Seq[Tables.ComponentProcessingStateRow]):Component={
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationId,cache)
    component.selectByComponentId(componentId,cache,assemblyNameMap,processingRecords)
  }
  def getComponentProcessingInfoForSimulation(simulationId: Int,simulationVersionId:Int)
  :Future[Seq[Tables.ComponentProcessingStateRow]] ={
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationId,cache)
    component.getComponentProcessingInfoForSimulation(simulationId,simulationVersionId, cache, assemblyNameMap)
  }

  def getComponentNameMapBySimulationId(simulationId:Int):Map[Int,String] ={
    component.selectComponentNameMapBySimulationId(simulationId,cache)
  }

  def getAssemblyRunningStatus(assemblyId:Int,simulationId:Int,simulationVersionId:Int):Future[Seq[Tables.ComponentProcessingStateRow]] ={
    assembly.getProcessingInfo(assemblyId,simulationId,simulationVersionId)
  }

  def incrementSimulationVersionDetails(simulationId:Int):Future[Int]={
    val flags = for{
      s <- simulation.incrementAndGetTheVersion(simulationId)
      a <- assembly.clearBusyOperationAsync(simulationId)

    }yield (s)

    flags

  }


  def getComponentWithProcessingInfo(componentId:Int,simulationId:Int,simulationVersionId:Int):Option[Component] = {
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationId,cache)
    component.selectByComponentSimulationId(componentId,simulationId,simulationVersionId , cache , assemblyNameMap)
  }

  def updateAssemblyOperationStatus(assemblyId: Int, operationId: Int, status: String): Boolean= {
    assembly.updateAssemblyOperationStatus(assemblyId, operationId, status,cache)
  }

  def updateComponentProcessingInfoInFailureScenarion(simulationId: Int,simulationVersionId:Int, componentId: Int, assemblyId: Int, sequence: Int, operationId: Int)={
    assembly.updateAssemblyOperationStatus(assemblyId,operationId,FreeOperationStatus.text,cache)
    component.updateComponentProcessingInfoForFailure(simulationId,simulationVersionId , componentId,assemblyId,sequence,operationId)
  }
  def updateComponentProcessingInfo(simulationId: Int,simulationVersionId:Int, componentId: Int, assemblyId: Int, sequence: Int, operationId: Int, failureWaitTime: Int) :Boolean ={
    assembly.updateAssemblyOperationStatus(assemblyId,operationId,FreeOperationStatus.text,cache)

    component.updateComponentProcessingInfo(simulationId,simulationVersionId,componentId,assemblyId,sequence,operationId , FinishedProcessingStatus,failureWaitTime)
  }
  def addComponentProcessingInfo(simId:Int,simulationVersionId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int , operationTime:Int):Boolean={
    component.addComponentProcessingInfo(simId,simulationVersionId,cmpId,assemblyId,sequence,opId,operationTime)
  }

  def getAllSimulation():List[Simulation] = {
    simulation.selectAllSimulations()
  }


  def getCompleteSimulationObject(simulationId:Int):Simulation = {

    val sim = simulation.getSimulationById(simulationId)
    val comps = simulation.getAllComponentIdsBySimulationId(simulationId).map(component.selectByComponentId(_,cache)).flatten
    val assemblies = simulation.getAllAssemblyIdsBySimulationId(simulationId).map(assembly.selectByAssemblyId(_,cache)).flatten
    sim.copy(components = comps,assemblies=assemblies)
  }

  def addAssembliesToSimulation(simulationId:Int,assemblyIds:List[Int]) = {
    simulation.addAssembliesToSimulation(simulationId,assemblyIds)
  }

  def addAssembly(a:Assembly):Int = {
    assembly.add(a)
  }


  def addComponentsToSimulation(simulationId:Int,componentsId:List[Int]) = {
    simulation.addComponentsToSimulation(simulationId,componentsId)
  }


  def addComponent(c:Component):Int = {
    component.add(c)
  }


  def addOperation(o:Operation):Int = {
      operation.add(o)
  }


  def addSimulation(name:String,desc:String):Int = {
    simulation.add(new Simulation(id=0,name = name,desc = desc , 1))
  }

  def getAssemblyMappedToSimulationId(assemblyId:Int, simulationId:Int):Option[Assembly]={
    assembly.selectByAssemblyId(assemblyId,cache) match {
      case Some(x) if simulation.isAssemblyMappedToSimulation(simulationId,x.id)=>{
        Some(x)
      }
      case _=> None
    }
  }

  def addAssemblyUrlToItsMappingEntry(simulationId:Int,assemblyId:Int,url:String) ={
    simulation.addAssemblyUrlToItsMappingEntry(simulationId,assemblyId,url)
  }
  def addComponentUrlToSimulationMapEntry(simulationId:Int, componentId:Int,url:String):Boolean={
    simulation.addComponentUrlToItsMappingEntry(simulationId,componentId,url)
  }

  override def getComponentMappedToSimulationId(componentId: Int, simulationId: Int): Option[Component] = {
    simulation.isComponentMappedToSimulation(simulationId, componentId) match{
      case true=> component.selectByComponentId(componentId,cache)
      case false => None
    }
  }

  def getAllAssembliesForSimulation(simulationId:Int):List[Assembly]={
    assembly.selectBySimulationId(simulationId,cache)
  }

  def getAllComponentUrlBySimulationId(simulationId:Int):List[(Int,String)] ={
    simulation.getAllComponentUrlBySimulationId(simulationId)
  }

  def getAllAssemblyUrlBySimulationId(simunlationId:Int):List[(Int,String)] =
  simulation.getAllAssemblyUrlBySimulationId(simunlationId)

//  def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit =
//    simulation.assignAssemblytoComponentSimulationMapping(assemblyId,ComponentId,simulationId)
}
