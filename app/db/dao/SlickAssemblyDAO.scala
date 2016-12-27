package db.dao
import dbgeneratedtable.Tables._
import models.Assembly
import slick.driver.MySQLDriver.profile.api._
import slick.lifted.TableQuery

/**
  * Created by billa on 26.12.16.
  */
class SlickAssemblyDAO(db:Database) extends AssemblyDao{

  private val operations = TableQuery[Operation]

  override def add(assembly: Assembly): Int = {

  }

  override def update(assembly: Assembly): Boolean = ???

  override def delete(assemblyId: Int): Boolean = ???

  override def selectAll(): List[Assembly] = ???

  override def selectBySimulationId(simulationId: Int): List[Assembly] = ???

  override def selectByAssemblyId(assemblyId: Int): Option[Assembly] = ???
}
