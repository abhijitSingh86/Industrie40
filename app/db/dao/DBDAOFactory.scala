package db.dao

import controllers.ApiResponse

/**
  * Created by billa on 26.12.16.
  */
trait SimulationDaoRepo{

  def simulation:SimulationDao

  trait SimulationDao{
    def getSimulationById(simulationId:Int):models.Simulation

    def selectAllSimulations():List[models.Simulation]

    def deleteSimulation(id:Int):Boolean

    def addComponentsToSimulation(simulationId:Int, componentsId:List[Int])

    def isComponentMappedToSimulation(simulationId:Int, componentId:Int):Boolean

    def isAssemblyMappedToSimulation(simulationId:Int, assemblyId:Int):Boolean

    def addComponentUrlToItsMappingEntry(simulationId:Int, componentId:Int,url:String):Boolean

    def addAssemblyUrlToItsMappingEntry(simulationId:Int, assemblyId:Int, url:String):Boolean

    def addAssembliesToSimulation(simunlationId:Int,assemblies:List[Int]):Unit

    def assignAssemblytoComponentSimulationMapping(assemblyId:Int,ComponentId:Int,simulationId:Int):Unit

    def getAllComponentUrlBySimulationId(simunlationId:Int):List[(Int,String)]

    def getAllAssemblyUrlBySimulationId(simunlationId:Int):List[(Int,String)]

    def add(simulation:models.Simulation):ApiResponse[Int]

  }
}
trait ComponentDaoRepo {

  def component: ComponentDao

  trait ComponentDao {

    def add(component: models.Component): Int

    def update(component: models.Component): Boolean

    def delete(componentId: Int): Boolean

    def selectAll(): List[models.Component]

    def selectBySimulationId1(simulationId: Int): List[models.Component]

    def selectByComponentId(componentId: Int): Option[models.Component]
  }

}
trait AssemblyDaoRepo{

  def assembly:AssemblyDao

  trait AssemblyDao {

    def add(assembly: models.Assembly): Int

    def update(assembly: models.Assembly): Boolean

    def delete(assemblyId: Int): Boolean

    def selectAllAssembly(): List[models.Assembly]

    def selectBySimulationId(simulationId: Int): List[models.Assembly]

    def selectByAssemblyId(assemblyId: Int): Option[models.Assembly]
  }
}

