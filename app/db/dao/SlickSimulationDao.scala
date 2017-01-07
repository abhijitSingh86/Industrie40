package db.dao

import db.DBComponent
import dbgeneratedtable.Tables
import dbgeneratedtable.Tables.{AssemblyOperationMappingRow, SimulationComponentMappingRow, SimulationassemblymapRow}
import models.ComponentOperation

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 27.12.16.
  */
trait SlickSimulationDao{
  this: DBComponent =>

  import driver.api._

  private lazy val simulations = Tables.Simulation

  private lazy val simulationComponentMapping = Tables.SimulationComponentMapping

  private lazy val simulationAssemblyMapping = Tables.Simulationassemblymap




  def selectByOperationId(simulationId:Int):models.Simulation ={
      Await.result(db.run(simulations.filter(_.id === simulationId).result.headOption),Duration.Inf ) match{
        case Some(x:Tables.SimulationRow) =>new models.Simulation(x.id,x.name,x.desc.getOrElse(""))
      }
  }

  def selectAllSimulations():List[models.Simulation] ={
    Await.result(db.run(simulations.result),Duration.Inf ) match{
      case x:IndexedSeq[Tables.OperationRow] =>x.map(y=> new models.Simulation(y.id,y.name,y.desc.getOrElse(""))).toList
    }
  }

  def deleteSimulation(id:Int):Boolean = {
    Await.result(db.run(simulations.filter(_.id === id).delete),Duration.Inf) match {
      case _:Int => true
      case _ => false
    }
  }

  def addComponentsToSimulation(simulationId:Int, componentsId:List[Int]) = {
    componentsId.map(x=>
      Await.result(db.run(simulationComponentMapping+= SimulationComponentMappingRow(simulationId,x)),Duration.Inf)
    )
  }

  def isComponentMappedToSimulation(simulationId:Int, componentId:Int):Boolean = {
    Await.result(db.run(simulationComponentMapping.filter(x => x.simulationId === simulationId && x.componentId === componentId).result),Duration.Inf).size > 0
  }

  def isAssemblyMappedToSimulation(simulationId:Int, assemblyId:Int):Boolean = {
    Await.result(db.run(simulationAssemblyMapping.filter(x => x.simulationId === simulationId && x.assemblyId === assemblyId).result),Duration.Inf).size > 0
  }

  def addComponentUrlToItsMappingEntry(simulationId:Int, componentId:Int,url:String):Boolean = {
    val q =  for{ x <- simulationComponentMapping if  x.simulationId === simulationId && x.componentId === componentId } yield x.url
    val updateAction = q.update(Some(url))

    Await.result(db.run(updateAction), Duration.Inf) == 1
  }

  def addAssemblyUrlToItsMappingEntry(simulationId:Int, assemblyId:Int, url:String):Boolean = {
    val q =  for{ x <- simulationAssemblyMapping if  x.simulationId === simulationId && x.assemblyId === assemblyId } yield x.url
    val updateAction = q.update(Some(url))

    Await.result(db.run(updateAction), Duration.Inf) == 1
  }

  def addAssembliesToSimulation(simunlationId:Int,assemblies:List[Int])={
    assemblies.map(assemblyId=>
    Await.result(db.run(simulationAssemblyMapping += SimulationassemblymapRow(simunlationId,assemblyId)),Duration.Inf))
  }

  def add(simulation:models.Simulation):Int = {
    val o = db.run(simulations returning simulations.map(_.id) += Tables.SimulationRow(0,simulation.name,Some(simulation.desc)))
    val k =Await.result(o , Duration.Inf)
    k
  }


}
