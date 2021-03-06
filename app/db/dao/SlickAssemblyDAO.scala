package db.dao

import java.util.Calendar

import db.{DBComponent, generatedtable}
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

    private lazy val assemblyFailureData = Tables.Assemblyfailuredata

    def addAssemblyFailureEntry(simulationId:Int,simulationVersionId:Int,assemblyId:Int,duration:Int):Long = {
      val stdate = Calendar.getInstance().getTimeInMillis
      val query = assemblyFailureData += new Tables.AssemblyfailuredataRow(simulationId,simulationVersionId,assemblyId,Some(duration),stdate,Some(stdate + duration*1000))
      Await.result(db.run(query),Duration.Inf)
      stdate
    }

    def addEndTimeInAssemblyFailureEntry(simulationId:Int,simulationVersionId:Int,assemblyId:Int,stdate:Long) = {
      val query =for{x <- assemblyFailureData if(x.simulationid === simulationId && x.version === simulationVersionId && x.assemblyid === assemblyId && x.starttime === stdate)} yield x.endtime
      val ettime = Calendar.getInstance().getTimeInMillis
      Await.result(db.run(query.update(Some(ettime))),Duration.Inf)
    }

    def getAssemblyFailureEntries(simulationId:Int,simulationVersionId:Int):Future[Seq[Tables.AssemblyfailuredataRow]] = {
      val query = assemblyFailureData.filter(x=> (x.simulationid === simulationId && x.version === simulationVersionId))
     db.run(query.result)
    }


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

    def getProcessingInfo(assemblyId:Int,simulationId:Int,simulationVersionId:Int):Future[Seq[Tables.ComponentProcessingStateRow]] ={
      val query = componentProcessingState.filter(x=> (x.assemblyid === assemblyId && x.simulationid === simulationId && x.version === simulationVersionId))
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
      val addedId = Await.result(db.run(assemblies returning assemblies.map(_.id) += new Tables.AssemblyRow(0, assembly.name, failurenumber = Some(assembly.fcount) , failuretime = Some(assembly.ftime) , iffailallowed = Some(assembly.ifFailAllowed))), Duration.Inf)
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

    def clearFailureData(simulationId: Int): Future[Boolean] = {
      val query = assemblyFailureData.filter(_.simulationid === simulationId).delete
      db.run(query).map(x=>true)
    }

//    def assemblyHeartBeatUpdateAsync(assemblyId: Int, simulationId: Int): Future[Boolean] = {
//      val query = for (c <- assemblies if ((c.id === assemblyId))) yield c.lastactive
//      db.run(query.update(Some(DateTimeUtils.getCurrentTimeStamp()))).map {
//        case 1 => true
//        case _ => false
//      }
//      Future
//    }

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

//          val isOnline = if (x.lastactive.isDefined) x.lastactive.get.after(DateTimeUtils.getOldBySecondsTS(6)) else false

          Some(new models.Assembly(x.id, x.name,x.failurenumber.getOrElse(0),x.failuretime.getOrElse(0), operations.toList,
            operations.filter(_.status == BusyOperationStatus).toList, x.iffailallowed.getOrElse(false)))

        }
        case None => {
          // do something in case of none
          None
        }
      }
  }

}
