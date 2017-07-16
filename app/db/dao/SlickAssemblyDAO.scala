package db.dao

import db.DBComponent
import db.generatedtable.Tables
import db.generatedtable.Tables.AssemblyOperationMappingRow
import models.{AssemblyOperation, AssemblyOperationStatus, BusyOperationStatus, FreeOperationStatus}
import play.api.cache.CacheApi
import utils.DateTimeUtils

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

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
    private lazy val componentProcessingState = Tables.ComponentProcessingState


    def selectAssemblyNameMapBySimulationId(simulationId: Int , cache: CacheApi): Map[Int,String] = {

      cache.getOrElse[Map[Int, String]](s"aname${simulationId}") {
        val query = for {s <- simulationAssemblyMapping
                         c <- assemblies
                         if (s.simulationId === simulationId && c.id === s.assemblyId)
        } yield c

        val res = Await.result(db.run(query.result), Duration.Inf)
        res.map(y => (y.id -> y.name)).toMap
      }
    }

    def getProcessingInfo(assemblyId:Int,simulationId:Int):Future[Seq[Tables.ComponentProcessingStateRow]] ={
      val query = componentProcessingState.filter(x=> (x.assemblyid === assemblyId && x.simulationid === simulationId))
        //for{x <- componentProcessingState if(x.assemblyid === assemblyId && x.simulationid === simulationId)} yield x
      db.run(query.sortBy(_.operationid).result)
    }
    override def updateAssemblyOperationStatus(assemblyId: Int, operationId: Int, status: String,cache:CacheApi): Boolean = {

      cache.remove(s"aom${assemblyId}");
      val dbStatus = for {x <- assemblyOperationMapping if ((x.assemblyId === assemblyId) && (x.operationId === operationId))} yield x.status
      Await.result(db.run(dbStatus.update(status)), Duration.Inf) match {
        case 1 => true
        case _ => false
      }
    }

    private def addAssemblyOperationMapping(assemblyId: Int, operations: List[AssemblyOperation]) = {
      operations.map(obj =>
        Await.result(db.run(assemblyOperationMapping += AssemblyOperationMappingRow(assemblyId, obj.operation.id,
          obj.time, FreeOperationStatus.text)), Duration.Inf)
      )
    }

    override def add(assembly: models.Assembly): Int = {
      val addedId = Await.result(db.run(assemblies returning assemblies.map(_.id) += new Tables.AssemblyRow(0, assembly.name)), Duration.Inf)
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

    def clearBusyOperationAsync(simulationId: Int): Future[Boolean] = {
      val query = assemblyOperationMapping.filter { aom =>
        aom.assemblyId in (
          simulationAssemblyMapping.filter(_.simulationId === simulationId) map (_.assemblyId)
          )
      }.map(_.status)

      db.run(query.update("free")).map {
        case a if a > 0 => true
        case _ => false
      }
    }

    def assemblyHeartBeatUpdateAsync(assemblyId: Int, simulationId: Int): Future[Boolean] = {
      val query = for (c <- assemblies if ((c.id === assemblyId))) yield c.lastactive
      db.run(query.update(Some(DateTimeUtils.getCurrentTimeStamp()))).map {
        case 1 => true
        case _ => false
      }
    }

    override def selectBySimulationId(simulationId: Int,cache:CacheApi): List[models.Assembly] = {
      Await.result(db.run(simulationAssemblyMapping.filter(_.simulationId === simulationId).result), Duration.Inf).map(x =>
        selectByAssemblyId(x.assemblyId,cache)).flatten.toList
    }

    override def selectByAssemblyId(assemblyId: Int, cache: CacheApi): Option[models.Assembly] = {

        Await.result(db.run(assemblies.filter(_.id === assemblyId).result.headOption)
          , Duration.Inf)
      } match {
        case Some(x) => {
          //transform into assembly object

//            cache.getOrElse[Seq[AssemblyOperation]](s"aom${assemblyId}") {
            val operations =Await.result(db.run(assemblyOperationMapping.filter(_.assemblyId === x.id).result), Duration.Inf).map(y =>
              AssemblyOperation(operation.selectByOperationId(y.operationId, cache), y.operationTime, AssemblyOperationStatus(y.status)))

          val isOnline = if (x.lastactive.isDefined) x.lastactive.get.after(DateTimeUtils.getOldBySecondsTS(6)) else false

          Some(new models.Assembly(x.id, x.name, operations.toList,
            operations.filter(_.status == BusyOperationStatus).toList, isOnline))

        }
        case None => {
          // do something in case of none
          None
        }
      }
  }

}
