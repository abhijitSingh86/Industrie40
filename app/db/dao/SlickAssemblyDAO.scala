package db.dao
import db.DBComponent
import dbgeneratedtable.Tables
import dbgeneratedtable.Tables.AssemblyOperationMappingRow

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 26.12.16.
  */
class SlickAssemblyDAO extends AssemblyDao{
  this: DBComponent =>
  import driver.api._

  private lazy val assemblies = Tables.Assembly

  override def add(assembly: models.Assembly): Int = {
    Await.result(db.run(assemblies returning assemblies.map(_.id)  += new Tables.AssemblyRow(0,assembly.name)),Duration.Inf)
  }


  override def update(assembly: models.Assembly): Boolean =
    Await.result(db.run(assemblies.filter(_.id === assembly.id).update(new Tables.AssemblyRow(assembly.id,assembly.name)))
      ,Duration.Inf)match{
    case 1 =>true
    case _ =>false
  }

  override def delete(assemblyId: Int): Boolean = Await.result(db.run(assemblies.filter(_.id === assemblyId).delete)
    ,Duration.Inf)match {
    case 1 => true
    case _ => false
  }

  override def selectAllAssembly(): List[models.Assembly] = Await.result(db.run(assemblies.result),Duration.Inf)match {
    case x:IndexedSeq[Tables.AssemblyRow] => {
      //convert all objects into Assembly object with other values
//      def create
    }
    case _ => List.empty[models.Assembly]
  }

  override def selectBySimulationId(simulationId: Int): List[models.Assembly] = ???

  override def selectByAssemblyId(assemblyId: Int): Option[models.Assembly] = Await.result(db.run(assemblies.filter(_.id === assemblyId).result.headOption)
    ,Duration.Inf)match{
    case Some(x) => {
      //transform into assembly object
    }
    case None => {
      // do something in case of none
      None
    }
  }
}
