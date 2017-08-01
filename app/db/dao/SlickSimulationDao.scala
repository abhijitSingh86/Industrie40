package db.dao

import controllers.ApiResponse
import db.{DBComponent, generatedtable}
import db.generatedtable.Tables
import db.generatedtable.Tables.{SimulationComponentMappingRow, SimulationassemblymapRow}
import models.{AssemblyTransportTime, ComponentToAssemblyTransTime}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by billa on 27.12.16.
  */
trait SlickSimulationDaoRepo extends SimulationDaoRepo{
  this: DBComponent =>

  override def simulation = new SlickSimulationDao()

  class SlickSimulationDao extends SimulationDao{

  import driver.api._

  private lazy val simulations = Tables.Simulation

  private lazy val simulationComponentMapping = Tables.SimulationComponentMapping

  private lazy val simulationAssemblyMapping = Tables.Simulationassemblymap

    private lazy val simulationAssemblyTT = Tables.Simulationa2atransporttime

    private lazy val simulationComponentTT = Tables.Simulationc2atransporttime


    def addAssemblyTimeMap(simulationId:Int,assemblyTransTime: List[AssemblyTransportTime]):Unit={
      assemblyTransTime.map(x=>{
        Await.result(db.run(simulationAssemblyTT+= new generatedtable.Tables.Simulationa2atransporttimeRow(x.assembly1,x.assembly2,simulationId,x.transportTime)), Duration.Inf)
      })
    }


    def getAssemblyTimeMap(simulationId:Int):List[AssemblyTransportTime] ={
      Await.result(db.run(simulationAssemblyTT.filter(_.simulationid === simulationId).result),Duration.Inf).map(
        x=>new AssemblyTransportTime(x.assemblyid1,x.assemblyid2,x.transporttime)
      ).toList
    }

    def getComponentTimeMap(simulationId:Int):List[ComponentToAssemblyTransTime] ={
      Await.result(db.run(simulationComponentTT.filter(_.simulationid === simulationId).result),Duration.Inf).map(
        x=>new ComponentToAssemblyTransTime(x.assemblyid,x.componentid,x.transporttime.getOrElse(0))
      ).toList
    }

    def addComponentTimeMap(simulationId:Int,componentToAssemblyTransTime: List[ComponentToAssemblyTransTime]):Unit={
      componentToAssemblyTransTime.map(x=>{
          Await.result(db.run(simulationComponentTT += new Tables.Simulationc2atransporttimeRow(x.assembly,x.component,simulationId,Some(x.transportTime))), Duration.Inf)
      })

    }

    def updateEndTime(simulationId:Int,etTime:Long):Boolean = {
      val q = for { x <- simulations if x.id === simulationId } yield x.endtime
      val query = q.update(Some(etTime))
      Await.result(db.run(query),Duration.Inf)  == 1
    }

    def updateStartTime(simulationId:Int,stTime:Long):Boolean = {
      val q = for { x <- simulations if x.id === simulationId } yield x.starttime
      val query = q.update(Some(stTime))
      Await.result(db.run(query),Duration.Inf)  == 1
    }

  def getSimulationById(simulationId: Int): models.Simulation = {
    Await.result(db.run(simulations.filter(_.id === simulationId).result.headOption), Duration.Inf) match {
      case Some(x: Tables.SimulationRow) => new models.Simulation(x.id, x.name, x.desc.getOrElse(""))
    }
  }

  def selectAllSimulations(): List[models.Simulation] = {
    Await.result(db.run(simulations.result), Duration.Inf) match {
      case x => x.sortWith(_.id > _.id).map(y => new models.Simulation(y.id, y.name, y.desc.getOrElse(""))).toList
    }
  }

  def deleteSimulation(id: Int): Boolean = {
    Await.result(db.run(simulations.filter(_.id === id).delete), Duration.Inf) match {
      case _: Int => true
      case _ => false
    }
  }

  def addComponentsToSimulation(simulationId: Int, componentsId: List[Int]) = {
    componentsId.map(x =>
      Await.result(db.run(simulationComponentMapping += SimulationComponentMappingRow(simulationId, x)), Duration.Inf)
    )
  }

  def isComponentMappedToSimulation(simulationId: Int, componentId: Int): Boolean = {
    Await.result(db.run(simulationComponentMapping.filter(x => x.simulationId === simulationId && x.componentId === componentId).result), Duration.Inf).size > 0
  }

  def isAssemblyMappedToSimulation(simulationId: Int, assemblyId: Int): Boolean = {
    Await.result(db.run(simulationAssemblyMapping.filter(x => x.simulationId === simulationId && x.assemblyId === assemblyId).result), Duration.Inf).size > 0
  }

  def addComponentUrlToItsMappingEntry(simulationId: Int, componentId: Int, url: String): Boolean = {
    val q = for {x <- simulationComponentMapping if x.simulationId === simulationId && x.componentId === componentId} yield x.url
    val updateAction = q.update(Some(url))

    Await.result(db.run(updateAction), Duration.Inf) == 1
  }

  def addAssemblyUrlToItsMappingEntry(simulationId: Int, assemblyId: Int, url: String): Boolean = {
    val q = for {x <- simulationAssemblyMapping if x.simulationId === simulationId && x.assemblyId === assemblyId} yield x.url
    val updateAction = q.update(Some(url))

    Await.result(db.run(updateAction), Duration.Inf) == 1
  }

  def addAssembliesToSimulation(simunlationId: Int, assemblies: List[Int]) = {
    assemblies.map(assemblyId =>
      Await.result(db.run(simulationAssemblyMapping += SimulationassemblymapRow(simunlationId, assemblyId)), Duration.Inf))
  }

//  def assignAssemblytoComponentSimulationMapping(assemblyId: Int, ComponentId: Int, simulationId: Int) = {
//    val q = for {c <- simulationComponentMapping if c.simulationId === simulationId && c.componentId === ComponentId} yield (c.assignedassemblyid)
//    val query = q.update(Some(assemblyId))
//    Await.result(db.run(query), Duration.Inf) == 1
//  }

  def getAllComponentUrlBySimulationId(simunlationId: Int): List[(Int, String)] = {
    Await.result(db.run(simulationComponentMapping.filter(_.simulationId === simunlationId).result), Duration.Inf).
      map(x => (x.componentId, x.url.getOrElse(""))).toList
  }

    def getAllComponentIdsBySimulationId(simunlationId: Int): List[Int] = {
      Await.result(db.run(simulationComponentMapping.filter(_.simulationId === simunlationId).result), Duration.Inf)
        .map(_.componentId).toList
    }

    def getAllAssemblyUrlBySimulationId(simunlationId: Int): List[(Int, String)] = {
    Await.result(db.run(simulationAssemblyMapping.filter(_.simulationId === simunlationId).result), Duration.Inf).
      map(x => (x.assemblyId, x.url.getOrElse(""))).toList
  }

    def getAllAssemblyIdsBySimulationId(simunlationId: Int): List[Int] = {
    Await.result(db.run(simulationAssemblyMapping.filter(_.simulationId === simunlationId).result), Duration.Inf).
      map(_.assemblyId).toList
  }

  def add(simulation: models.Simulation):Int = {
    val o = db.run(simulations returning simulations.map(_.id) += Tables.SimulationRow(0, simulation.name, Some(simulation.desc)))
//    ApiResponse.Async.Right(o)
    Await.result(o,Duration.Inf)
  }
}

}
