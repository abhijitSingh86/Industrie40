package db.dao

import db.DBComponent
import dbgeneratedtable.Tables
import dbgeneratedtable.Tables.ComponentProcessingStateRow
import models._
import utils.DateTimeUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
/**
  * Created by billa on 2016-12-24.
  */
trait SlickComponentDaoRepo extends ComponentDaoRepo {
  this: SlickOperationDaoRepo with DBComponent =>

  def component = new SlickComponentDao()

  class SlickComponentDao extends ComponentDao {

    import driver.api._

    val components = TableQuery[Tables.Component]
    val componentsOperationMapping = TableQuery[Tables.ComponentOperationMapping]
    val componentProcessingState = TableQuery[Tables.ComponentProcessingState]


    def addComponentProcessingInfo(simId:Int,cmpId:Int,assemblyId:Int,sequence:Int,opId:Int):Boolean={
      val result  = db.run(componentProcessingState += new ComponentProcessingStateRow(cmpId,simId,sequence,opId,
        Some(DateTimeUtils.getCurrentTimeStamp()),None,assemblyId))

      Await.result(result,Duration.Inf) match {
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


    private def createProcessingSequenceList(row: List[Tables.ComponentOperationMappingRow]): List[ProcessingSequence] = {
      def createList(row: List[Tables.ComponentOperationMappingRow]) = {
        val maximumSequenceNum = row.maxBy(x => x.sequence).sequence
        val listSize = row.size / maximumSequenceNum
        val sortedRow = row.sortBy(_.sequence)
        var counter = 0
        (for (_ <- 0 until listSize) yield ({
          val arr = for (a <- counter until sortedRow.size by listSize) yield ({
            operation.selectByOperationId(sortedRow(a).operationId)
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

    override def selectByComponentId(componentId: Int): Option[Component] = {
      Await.result(db.run(components.filter(_.id === componentId).result.headOption), Duration.Inf) match {
        case Some(x) => {
          Await.result(db.run(componentsOperationMapping.filter(_.componentId === x.id).result), Duration.Inf) match {
            case row => { //:List[ComponentOperationMappingRow]
              val processingSequenceList = createProcessingSequenceList(row.toList)

              //Empty details filled because it is queried without simulation data
//              val (completedOperations:List[Operation],componentSchedulingInfo:ComponentSchedulingInfo) =
//                (List[Operation](),ComponentSchedulingInfo(List[OperationProcessingInfo]() , None,0))

              Some(Component(x.id, x.name, processingSequenceList ,EmptySchedulingInfo))
            }
            case _ => None
          }
        }
      }
    }

    def mapToOperationProcessingInfo(x: Tables.ComponentProcessingStateRow) = {
      new OperationProcessingInfo(x.operationid,x.assemblyid,x.startTime.get.getTime,x.endTime.get.getTime)
    }

    def createComponentSchedulingInfo(componentId: Int, simulationId: Int):ComponentSchedulingInfo = {
      //TODO check for sort be descending after some values
        val result = db.run(componentProcessingState.filter(x=> (x.componentid === componentId && x.simulationid === simulationId)).sortBy(_.sequencenum).result).map(y=>
        {
          //get each row and form the scheduling information Details
            var oinfo:List[OperationProcessingInfo] = List()
            val curr = if(y.take(1)(0).endTime.isDefined){
              oinfo = y.map(x=>mapToOperationProcessingInfo(x)
              ).toList
              None
            } else{
              oinfo = y.takeRight(y.length-1).map(x=>mapToOperationProcessingInfo(x)
              ).toList

              val c=y.take(1)(0)
              Some(new OperationProcessingInfo(c.operationid,c.assemblyid,c.startTime.get.getTime,0l))
            }

          val completedOPerationList = y.map(_.operationid).reverse.map(operation.selectByOperationId(_)).toList
          new ComponentSchedulingInfo(oinfo,curr,y.take(1)(0).sequencenum,completedOPerationList)
        })
      Await.result(result,Duration.Inf)
    }

    def selectByComponentSimulationId(componentId: Int, simulationId:Int): Option[Component] = {
      Await.result(db.run(components.filter(_.id === componentId).result.headOption), Duration.Inf) match {
        case Some(x) => {
          Await.result(db.run(componentsOperationMapping.filter(_.componentId === x.id).result), Duration.Inf) match {
            case row => { //:List[ComponentOperationMappingRow]
              val processingSequenceList = createProcessingSequenceList(row.toList)

              //Empty details filled because it is queried without simulation data
              val componentSchedulingInfo:ComponentSchedulingInfo=
                createComponentSchedulingInfo(componentId,simulationId)

              Some(Component(x.id, x.name, processingSequenceList , componentSchedulingInfo))
            }
            case _ => None
          }
        }
      }
    }
    //---------------------simulation id based database methods

    override def selectBySimulationId1(simulationId: Int): List[Component] = ???

    //    Await.result(db.run(components.filter(_.id === componentId).result),Duration.Inf) match {
    //      case x:Tables.ComponentRow=> {
    //        Await.result(db.run(componentsOperationMapping.filter(_.componentId === x.id).result),Duration.Inf) match{
    //          case row:List[ComponentOperationMappingRow] => {
    //            Some(Component(x.id, x.name, PriorityEnum.NORMAL, createProcessingSequenceList(row)))
    //          }
    //          case _ => None
    //        }
    //      }
    //    }
    //  }
  }

}
