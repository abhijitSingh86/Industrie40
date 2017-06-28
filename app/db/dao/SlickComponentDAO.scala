package db.dao

import db.DBComponent
import dbgeneratedtable.Tables
import dbgeneratedtable.Tables.ComponentProcessingStateRow
import models._
import play.api.cache.CacheApi
import utils.DateTimeUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

/**
  * Created by billa on 2016-12-24.
  */
trait SlickComponentDaoRepo extends ComponentDaoRepo {
  this: SlickOperationDaoRepo with DBComponent =>

  def component = new SlickComponentDao()

  class SlickComponentDao extends ComponentDao {

    import driver.api._

    private lazy val components = Tables.Component
    private lazy val componentsOperationMapping = Tables.ComponentOperationMapping
    private lazy val componentProcessingState = Tables.ComponentProcessingState
    private lazy val simulationComponentMapping = Tables.SimulationComponentMapping

    def clearComponentProcessingDetailsAsync(simulationId: Int): Future[Boolean] = {
      db.run(componentProcessingState.filter(_.simulationid === simulationId).delete).map {
        case a: Int if a > 0 => true
        case _ => false
      }
    }

    def componentHeartBeatUpdateAsync(componentId: Int, simulationId: Int): Future[Boolean] = {
      val query = for (c <- components if ((c.id === componentId))) yield c.last_active
      db.run(query.update(Some(DateTimeUtils.getCurrentTimeStamp()))).map {
        case 1 => true
        case _ => false
      }
    }

    def updateComponentProcessingInfo(simId: Int, cmpId: Int, assemblyId: Int, sequence: Int, opId: Int): Boolean = {
      val res = for {c <- componentProcessingState if (c.assemblyid === assemblyId && c.componentid === cmpId &&
        c.operationid === opId && c.simulationid === simId && c.sequencenum === sequence)} yield c.endTime

      Await.result(db.run(res.update(Some(DateTimeUtils.getCurrentTimeStamp()))), Duration.Inf) match {
        case 1 => true
        case _ => false
      }
    }

    def addComponentProcessingInfo(simId: Int, cmpId: Int, assemblyId: Int, sequence: Int, opId: Int): Boolean = {
      val result = db.run(componentProcessingState += new ComponentProcessingStateRow(cmpId, simId, sequence, opId,
        Some(DateTimeUtils.getCurrentTimeStamp()), None, assemblyId))

      Await.result(result, Duration.Inf) match {
        case 1 => true
        case _ => false
      }
    }

    override def add(component: Component): Int = {
      val addedId = Await.result(db.run((components returning components.map(_.id) += Tables.ComponentRow(component.id, component.name))), Duration.Inf)
      addedId match {
        case id => {
          component.processingSequences.map(l => {
            var seqCounter = 1
            l.seq.map(x => {
              Await.result(db.run(componentsOperationMapping += Tables.ComponentOperationMappingRow(id, x.id, seqCounter)), Duration.Inf)
              seqCounter = seqCounter + 1
            })
          })
          id
        }
      }

    }

    override def update(component: Component): Boolean = {
      Await.result(db.run(components.filter(_.id === component.id).update(Tables.ComponentRow(component.id, component.name))), Duration.Inf) match {
        case x: Int if (x == 1) => true
        case _ => false
      }
    }


    override def delete(componentId: Int): Boolean = {
      Await.result(db.run(components.filter(_.id === componentId).delete), Duration.Inf) match {
        case x: Int if (x == 1) => true
        case _ => false
      }
    }


    private def createProcessingSequenceList(row: List[Tables.ComponentOperationMappingRow], cache: CacheApi): List[ProcessingSequence] = {
      def createList(row: List[Tables.ComponentOperationMappingRow]) = {
        val maximumSequenceNum = row.maxBy(x => x.sequence).sequence
        val listSize = row.size / maximumSequenceNum
        val sortedRow = row.sortBy(_.sequence)
        var counter = 0
        (for (_ <- 0 until listSize) yield ({
          val arr = for (a <- counter until sortedRow.size by listSize) yield ({
            operation.selectByOperationId(sortedRow(a).operationId, cache)
          })
          counter = counter + 1
          new ProcessingSequence(
            arr.toList)
        })).toList
      }

      if (row.size > 0) {
        createList(row);
      } else {
        List[ProcessingSequence]()
      }
    }

    def getAllComponentsByIds(ids:List[Int]):Future[Component] = {
      val query = for{
        c <- components
        if c.id inSetBind ids
      }yield c

      db.run()
    }

    override def selectAll(): List[Component] = ???

    //    {
    //      Await.result(db.run(components.result), Duration.Inf) match {
    //        case x => {
    //          x.map(y => {
    //            Await.result(db.run(componentsOperationMapping.filter(_.componentId === y.id).result), Duration.Inf) match {
    //              case row: IndexedSeq[ComponentOperationMappingRow] => {
    //                Component(y.id, y.name, PriorityEnum.NORMAL, createProcessingSequenceList(row.toList))
    //              }
    //            }
    //          }).toList
    //
    //        }
    //      }
    //    }

    override def selectByComponentId(componentId: Int, cache: CacheApi): Option[Component] = {
      Await.result(db.run(components.filter(_.id === componentId).result.headOption), Duration.Inf) match {
        case Some(x) => {
          val processingSequenceList = getProcessingInfo(componentId,cache)
          Some(Component(x.id, x.name, processingSequenceList, EmptySchedulingInfo))
        }
        case _ => None
      }
    }

    def mapToOperationProcessingInfo(x: Tables.ComponentProcessingStateRow,assmeblyName:String) = {
      new OperationProcessingInfo(x.operationid, x.assemblyid, assmeblyName ,x.startTime.get.getTime, x.endTime.get.getTime)
    }

    def createComponentSchedulingInfo(componentId: Int, simulationId: Int, cache: CacheApi , assemblyNameMap:Map[Int,String]): ComponentSchedulingInfo = {
      //TODO check for sort be descending after some values
      val result = db.run(componentProcessingState.filter(x => (x.componentid === componentId &&
        x.simulationid === simulationId)).sortBy(_.sequencenum.desc).result).map(y => {
        val firstRow = if (y.takeRight(1).size == 1) Some(y.take(1)(0)) else None
        //get each row and form the scheduling information Details
        var oinfo: List[OperationProcessingInfo] = List()

        val curr = if (firstRow.isDefined && firstRow.get.endTime.isDefined) {
          oinfo = y.map(x => mapToOperationProcessingInfo(x ,  assemblyNameMap.get(x.assemblyid).getOrElse(""))
          ).toList
          None
        } else if (firstRow.isDefined) {
          oinfo = y.takeRight(y.length - 1).map(x => mapToOperationProcessingInfo(x ,  assemblyNameMap.get(x.assemblyid).getOrElse(""))
          ).toList

          val c = firstRow.get
          Some(new OperationProcessingInfo(c.operationid, c.assemblyid, assemblyNameMap.get(c.assemblyid).getOrElse("") , c.startTime.get.getTime, 0l))
        } else {
          None
        }

        val sequemce = if (firstRow.isDefined) firstRow.get.sequencenum + 1 else 0

        val completedOPerationList = oinfo.map(_.operationId).reverse.map(operation.selectByOperationId(_, cache)).toList

        new ComponentSchedulingInfo(oinfo.reverse, curr, sequemce, completedOPerationList)
      })
      Await.result(result, Duration.Inf)
    }

    private def isOnline(x:Tables.ComponentRow):Boolean ={
      if (x.last_active.isDefined) x.last_active.get.after(DateTimeUtils.getOldBySecondsTS(6)) else false
    }

    private def getProcessingInfo(cid:Int , cache:CacheApi) = {
      cache.getOrElse[List[ProcessingSequence]](s"c${cid}") {
        Await.result(db.run(componentsOperationMapping.filter(_.componentId === cid).result), Duration.Inf) match {
          case row => { //:List[ComponentOperationMappingRow]
            createProcessingSequenceList(row.toList, cache)
          }
          case _ => List[ProcessingSequence]()
        }
      }
    }


    def selectByComponentSimulationId(componentId: Int, simulationId: Int, cache: CacheApi,assemblyNameMap:Map[Int,String]): Option[Component] = {
      Await.result(db.run(components.filter(_.id === componentId).result.headOption), Duration.Inf) match {
        case Some(x) => {
          val processingSequenceList = getProcessingInfo(componentId,cache)
          val componentSchedulingInfo: ComponentSchedulingInfo = createComponentSchedulingInfo(componentId, simulationId, cache , assemblyNameMap)
          Some(Component(x.id, x.name, processingSequenceList, componentSchedulingInfo, isOnline(x)))
        }
        case _ => None
      }
    }

    //---------------------simulation id based database methods

    override def selectComponentNameMapBySimulationId(simulationId: Int, cache: CacheApi): Map[Int, String] = {

      cache.getOrElse[Map[Int, String]](s"cname${simulationId}") {
        val query = for {s <- simulationComponentMapping
                         c <- components
                         if (s.simulationId === simulationId && c.id === s.componentId)
        } yield c

        val res = Await.result(db.run(query.result), Duration.Inf)
        res.map(y => (y.id -> y.name)).toMap
      }
    }
  }

}
