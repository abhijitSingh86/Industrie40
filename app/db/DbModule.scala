package db

import db.dao._
import dbgeneratedtable.Tables
import models._
import play.api.cache.CacheApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by billa on 25.04.17.
  */
trait DbModule {

  def componentHeartBeatUpdateAsync(componentId:Int,simulationId:Int):Future[Boolean]

  def getSimulation(simulationId:Int):Simulation

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

  def getComponentWithProcessingInfo(componentId:Int,simulationId:Int):Option[Component]

  def addComponentsToSimulation(simulationId:Int,componentsId:List[Int])

  def addAssembliesToSimulation(simulationId:Int,assemblyIds:List[Int])

  def addAssembly(a:Assembly):Int

  def addComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean

  def updateAssemblyOperationStatus(assemblyId:Int, operationId:Int, status:String):Boolean

  def updateComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean

  def clearPreviousSimulationProcessingDetails(simulationId:Int):Future[Boolean]

  def assemblyHeartBeatUpdateAsync(assemblyId:Int, simulationId:Int):Future[Boolean]

  def getAssemblyRunningStatus(assemblyId:Int,simulationId:Int):Future[Seq[Tables.ComponentProcessingStateRow]]

  def getComponentNameMapBySimulationId(simulationId:Int):Map[Int,String]

  def getComponentProcessingInfoForSimulation(simulationId: Int)
  :Future[Seq[Tables.ComponentProcessingStateRow]]

  def getComponentById(componentId:Int ,simulationId:Int, processingRecords:Seq[Tables.ComponentProcessingStateRow]):Component
}

class SlickModuleImplementation(cache:CacheApi) extends DbModule {

  this: SimulationDaoRepo
    with AssemblyDaoRepo
    with ComponentDaoRepo
    with OperationDaoRepo
    with DBComponent =>

  def getComponentById(componentId:Int ,simulationId:Int, processingRecords:Seq[Tables.ComponentProcessingStateRow]):Component={
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationId,cache)
    component.selectByComponentId(componentId,cache,assemblyNameMap,processingRecords)
  }
  def getComponentProcessingInfoForSimulation(simulationId: Int)
  :Future[Seq[Tables.ComponentProcessingStateRow]] ={
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationId,cache)
    component.getComponentProcessingInfoForSimulation(simulationId, cache, assemblyNameMap)
  }

  def getComponentNameMapBySimulationId(simulationId:Int):Map[Int,String] ={
    component.selectComponentNameMapBySimulationId(simulationId,cache)
  }

  def getAssemblyRunningStatus(assemblyId:Int,simulationId:Int):Future[Seq[Tables.ComponentProcessingStateRow]] ={
    assembly.getProcessingInfo(assemblyId,simulationId)
  }

  def clearPreviousSimulationProcessingDetails(simulationId:Int):Future[Boolean]={
    val flags = for{
      c <- component.clearComponentProcessingDetailsAsync(simulationId)
      a <- assembly.clearBusyOperationAsync(simulationId)
    }yield (c,a)
    flags map{
      case (true,_) => true
      case (_,true) => true
      case _ => false
    }
  }

  def componentHeartBeatUpdateAsync(componentId:Int,simulationId:Int):Future[Boolean] = {
    component.componentHeartBeatUpdateAsync(componentId,simulationId)
  }

  def assemblyHeartBeatUpdateAsync(assemblyId:Int, simulationId:Int):Future[Boolean] = {
    assembly.assemblyHeartBeatUpdateAsync(assemblyId,simulationId)
  }

  def getComponentWithProcessingInfo(componentId:Int,simulationId:Int):Option[Component] = {
    val assemblyNameMap = assembly.selectAssemblyNameMapBySimulationId(simulationId,cache)
    component.selectByComponentSimulationId(componentId,simulationId,cache , assemblyNameMap)
  }

  def updateAssemblyOperationStatus(assemblyId: Int, operationId: Int, status: String): Boolean= {
    assembly.updateAssemblyOperationStatus(assemblyId, operationId, status,cache)
  }

  def updateComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean ={
    assembly.updateAssemblyOperationStatus(assemblyId,opId,FreeOperationStatus.text,cache)
    component.updateComponentProcessingInfo(simId,cmpId,assemblyId,sequence,opId)
  }
  def addComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean={
    component.addComponentProcessingInfo(simId,cmpId,assemblyId,sequence,opId)
  }

  def getAllSimulation():List[Simulation] = {
    simulation.selectAllSimulations()
  }


  def getSimulation(simulationId:Int):Simulation = {

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
    simulation.add(new Simulation(id=0,name = name,desc = desc))
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
