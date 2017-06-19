package db.dao

import dbgeneratedtable.Tables
import models.{Component, Operation}
import play.api.cache.CacheApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by billa on 26.12.16.
  */

trait OperationDaoRepo {
  def operation:OperationDao

  trait OperationDao {
    def add(operation: Operation): Int
    def deleteOperation(id:Int):Boolean
    def selectAllOperations():List[Operation]
    def selectByOperationId(operationId:Int,cache:CacheApi):Operation
    }

}

trait SimulationDaoRepo{

  def simulation:SimulationDao

  trait SimulationDao{

    def getAllComponentIdsBySimulationId(simunlationId: Int): List[Int]

    def getAllAssemblyIdsBySimulationId(simunlationId: Int): List[Int]

    def getSimulationById(simulationId:Int):models.Simulation

    def selectAllSimulations():List[models.Simulation]

    def deleteSimulation(id:Int):Boolean

    def addComponentsToSimulation(simulationId:Int, componentsId:List[Int])

    def isComponentMappedToSimulation(simulationId:Int, componentId:Int):Boolean

    def isAssemblyMappedToSimulation(simulationId:Int, assemblyId:Int):Boolean

    def addComponentUrlToItsMappingEntry(simulationId:Int, componentId:Int,url:String):Boolean

    def addAssemblyUrlToItsMappingEntry(simulationId:Int, assemblyId:Int, url:String):Boolean

    def addAssembliesToSimulation(simunlationId:Int,assemblies:List[Int]):Unit

//    def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit

    def getAllComponentUrlBySimulationId(simunlationId:Int):List[(Int,String)]

    def getAllAssemblyUrlBySimulationId(simunlationId:Int):List[(Int,String)]

    def add(simulation:models.Simulation):Int



  }
}
trait ComponentDaoRepo {

  def component: ComponentDao

  trait ComponentDao {

    def add(component: models.Component): Int

    def update(component: models.Component): Boolean

    def delete(componentId: Int): Boolean

    def selectAll(): List[models.Component]

    def selectComponentNameMapBySimulationId(simulationId: Int , cache: CacheApi): Map[Int,String]

    def selectByComponentId(componentId: Int,cache:CacheApi): Option[models.Component]

    def selectByComponentSimulationId(componentId: Int, simulationId:Int,cache:CacheApi,assemblyNameMap:Map[Int,String]): Option[models.Component]

    def addComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean

    def updateComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean

    def componentHeartBeatUpdateAsync(componentId:Int,simulationId:Int):Future[Boolean]

    def clearComponentProcessingDetailsAsync(simulationId:Int):Future[Boolean]
  }

}
trait AssemblyDaoRepo{

  def assembly:AssemblyDao

  trait AssemblyDao {

    def selectAssemblyNameMapBySimulationId(simulationId: Int , cache: CacheApi): Map[Int,String]

    def getProcessingInfo(assemblyId:Int,simulationId:Int):Future[Seq[Tables.ComponentProcessingStateRow]]

    def assemblyHeartBeatUpdateAsync(assemblyId: Int, simulationId: Int):Future[Boolean]

    def clearBusyOperationAsync(simulationId:Int):Future[Boolean]

    def updateAssemblyOperationStatus(assemblyId:Int, operationId:Int, status:String,cache:CacheApi):Boolean

    def add(assembly: models.Assembly): Int

    def update(assembly: models.Assembly): Boolean

    def delete(assemblyId: Int): Boolean

    def selectAllAssembly(): List[models.Assembly]

    def selectBySimulationId(simulationId: Int,cache:CacheApi): List[models.Assembly]

    def selectByAssemblyId(assemblyId: Int ,cache:CacheApi): Option[models.Assembly]
  }
}

