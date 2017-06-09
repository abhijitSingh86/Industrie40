package db

import db.dao._
import models._

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
}

class SlickModuleImplementation extends DbModule {

  this: SimulationDaoRepo
    with AssemblyDaoRepo
    with ComponentDaoRepo
    with OperationDaoRepo
    with DBComponent =>


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

  def getComponentWithProcessingInfo(componentId:Int,simulationId:Int):Option[Component] = {
    component.selectByComponentSimulationId(componentId,simulationId)
  }

  def updateAssemblyOperationStatus(assemblyId: Int, operationId: Int, status: String): Boolean= {
    assembly.updateAssemblyOperationStatus(assemblyId, operationId, status)
  }

  def updateComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean ={
    assembly.updateAssemblyOperationStatus(assemblyId,opId,FreeOperationStatus.text)
    component.updateComponentProcessingInfo(simId,cmpId,assemblyId,sequence,opId)
  }
  def addComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean={
    component.addComponentProcessingInfo(simId,cmpId,assemblyId,sequence,opId)
  }

  def getAllSimulation():List[Simulation] = {
    simulation.selectAllSimulations().map(x=> getSimulation(x.id))
  }


  def getSimulation(simulationId:Int):Simulation = {

    val sim = simulation.getSimulationById(simulationId)
    val comps = simulation.getAllComponentIdsBySimulationId(simulationId).map(component.selectByComponentId(_)).flatten
    val assemblies = simulation.getAllAssemblyIdsBySimulationId(simulationId).map(assembly.selectByAssemblyId(_)).flatten
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
    assembly.selectByAssemblyId(assemblyId) match {
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
      case true=> component.selectByComponentId(componentId)
      case false => None
    }
  }

  def getAllAssembliesForSimulation(simulationId:Int):List[Assembly]={
    assembly.selectBySimulationId(simulationId)
  }

  def getAllComponentUrlBySimulationId(simulationId:Int):List[(Int,String)] ={
    simulation.getAllComponentUrlBySimulationId(simulationId)
  }

  def getAllAssemblyUrlBySimulationId(simunlationId:Int):List[(Int,String)] =
  simulation.getAllAssemblyUrlBySimulationId(simunlationId)

//  def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit =
//    simulation.assignAssemblytoComponentSimulationMapping(assemblyId,ComponentId,simulationId)
}
