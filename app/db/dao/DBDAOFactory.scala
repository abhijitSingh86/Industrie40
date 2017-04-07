package db.dao

import models.{Assembly, Component}

/**
  * Created by billa on 26.12.16.
  */

trait DBDAOFactory {

  def getComponentObject():ComponentDao

  def getAssemblyObject():AssemblyDao

}

class SlickDAOFactory extends DBDAOFactory{

  override def getComponentObject(): ComponentDao = ???

  override def getAssemblyObject(): AssemblyDao = ???
}

//=========================================

trait ComponentDao{
  def add(component:models.Component):Int
  def update(component:models.Component):Boolean
  def delete(componentId:Int):Boolean
  def selectAll():List[models.Component]
  def selectBySimulationId(simulationId:Int):List[models.Component]
  def selectByComponentId(componentId:Int):Option[models.Component]
}

trait AssemblyDao{
  def add(assembly:models.Assembly) : Int
  def update(assembly: models.Assembly):Boolean
  def delete(assemblyId:Int):Boolean
  def selectAllAssembly():List[models.Assembly]
  def selectBySimulationId(simulationId:Int):List[models.Assembly]
  def selectByAssemblyId(assemblyId:Int):Option[models.Assembly]
}

