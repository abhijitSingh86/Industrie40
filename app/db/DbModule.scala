package db

import db.dao._
import models.{Assembly, Component}

/**
  * Created by billa on 25.04.17.
  */
trait DbModule {

  def getComponentMappedToSimulationId(componentId:Int, simulationId:Int):Option[Component]

  def addComponentUrlToSimulationMapEntry(simulationId:Int, componentId:Int,url:String):Boolean

  def getAllAssembliesForSimulation(simulationid:Int):List[Assembly]

  def getAllComponentUrlBySimulationId(simulationId:Int):List[(Int,String)]

  def getAllAssemblyUrlBySimulationId(simunlationId:Int):List[(Int,String)]

  def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit
}

class SlickModuleImplementation extends DbModule {

  this: SimulationDaoRepo
    with AssemblyDaoRepo
    with ComponentDaoRepo
    with SlickOperationDao
    with MySqlDBComponent =>


  def addComponentUrlToSimulationMapEntry(simulationId:Int, componentId:Int,url:String):Boolean={
    simulation.addComponentUrlToItsMappingEntry(simulationId,componentId,url)
  }

  override def getComponentMappedToSimulationId(componentId: Int, simulationId: Int): Option[Component] = {
    component.selectByComponentId(componentId) match {
      //check if this exist for simulation ID
      case Some(x) if simulation.isComponentMappedToSimulation(simulationId, x.id) => {
        Some(x)
      }
      case _ => None
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

  def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit =
    simulation.assignAssemblytoComponentSimulationMapping(assemblyId,ComponentId,simulationId)
}
