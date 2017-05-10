package db.dao

import db.DBComponent
import dbgeneratedtable.Tables
import dbgeneratedtable.Tables.AssemblyOperationMappingRow

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 26.12.16.
  */
trait SlickAssemblyDaoRepo extends AssemblyDaoRepo {

  this: OperationDaoRepo with DBComponent =>

  def assembly: SlickAssemblyDAO = new SlickAssemblyDAO()

  class SlickAssemblyDAO extends AssemblyDao {


    import driver.api._

    private lazy val assemblies = Tables.Assembly
    private lazy val assemblyOperationMapping = Tables.AssemblyOperationMapping
    private lazy val simulationAssemblyMapping = Tables.Simulationassemblymap


    def addAssemblyOperationMapping(assemblyId: Int, operations: List[(models.Operation, Int)]) = {
      operations.map(obj =>
        db.run(assemblyOperationMapping += AssemblyOperationMappingRow(assemblyId, obj._1.id, obj._2))
      )
    }

    override def add(assembly: models.Assembly): Int = {
      val addedId= Await.result(db.run(assemblies returning assemblies.map(_.id) += new Tables.AssemblyRow(0, assembly.name)), Duration.Inf)
      addedId match {
        case id: Int => {
          addAssemblyOperationMapping(id, assembly.totalOperations)
          id
        }
      }
      addedId
    }


    override def update(assembly: models.Assembly): Boolean =
      Await.result(db.run(assemblies.filter(_.id === assembly.id).update(new Tables.AssemblyRow(assembly.id, assembly.name)))
        , Duration.Inf) match {
        case 1 => true
        case _ => false
      }

    override def delete(assemblyId: Int): Boolean = Await.result(db.run(assemblies.filter(_.id === assemblyId).delete)
      , Duration.Inf) match {
      case 1 => true
      case _ => false
    }

    override def selectAllAssembly(): List[models.Assembly] = ???

    //  Await.result(db.run(assemblies.result),Duration.Inf)match {
    //    case x:IndexedSeq[Tables.AssemblyRow] => {
    //      //convert all objects into Assembly object with other values
    ////      def create
    //    }
    //    case _ => List.empty[models.Assembly]
    //  }

    override def selectBySimulationId(simulationId: Int): List[models.Assembly] = {
      Await.result(db.run(simulationAssemblyMapping.filter(_.simulationId === simulationId).result), Duration.Inf).map(x =>
        selectByAssemblyId(x.assemblyId)).flatten.toList
    }

    override def selectByAssemblyId(assemblyId: Int): Option[models.Assembly] = {
      Await.result(db.run(assemblies.filter(_.id === assemblyId).result.headOption)
        , Duration.Inf) match {
        case Some(x) => {
          //transform into assembly object
          val operations = Await.result(db.run(assemblyOperationMapping.filter(_.assemblyId === x.id).result), Duration.Inf).map(y =>
            (operation.selectByOperationId(y.operationId), y.operationTime))
          Some(new models.Assembly(x.id, x.name, operations.toList))

        }
        case None => {
          // do something in case of none
          None
        }
      }
    }
  }

}
