package db.dao

import db.DBComponent
import dbgeneratedtable.Tables.ComponentOperationMappingRow
import dbgeneratedtable._
import enums.PriorityEnum
import models.{Component, ProcessingSequence}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
/**
  * Created by billa on 2016-12-24.
  */
class SlickComponentDAO extends ComponentDao{
  this: SlickOperationDao with  DBComponent  =>

  import driver.api._

  val components = TableQuery[Tables.Component]
  val componentsOperationMapping = TableQuery[Tables.ComponentOperationMapping]

  override def add(component: Component): Int = {
    val addedId = Await.result(db.run((components returning components.map(_.id) += Tables.ComponentRow(component.id,component.name))),Duration.Inf)
     addedId match {
      case id  => {
        component.processingSequences.map(l => {
          var seqCounter = 1
          l.seq.map(x => {
            Await.result(db.run(componentsOperationMapping +=ComponentOperationMappingRow(id,x.id,seqCounter)),Duration.Inf)
            seqCounter=seqCounter+1
          } )
        })
        id
      }
    }

  }

  override def update(component: Component): Boolean = {
    Await.result(db.run(components.filter(_.id === component.id).update(Tables.ComponentRow(component.id, component.name))) ,Duration.Inf) match {
      case x:Int  if(x==1)=> true
      case _ => false
    }
  }


  override def delete(componentId: Int): Boolean = {
    Await.result(db.run(components.filter(_.id === componentId).delete) ,Duration.Inf) match {
      case x:Int  if(x==1)=> true
      case _ => false
    }
  }



  private def createProcessingSequenceList(row: List[_root_.dbgeneratedtable.Tables.ComponentOperationMappingRow]):List[ProcessingSequence] = {
    def createList(row: List[_root_.dbgeneratedtable.Tables.ComponentOperationMappingRow]) = {
      val maximumSequenceNum = row.maxBy(x => x.sequence).sequence
      val listSize = row.size / maximumSequenceNum
      val sortedRow = row.sortBy(_.sequence)
      var counter = 0
      (for (_ <- 0 until listSize) yield ({
        val arr = for (a <- counter until sortedRow.size by listSize) yield ({
          selectByOperationId(sortedRow(a).operationId)
        })
        counter = counter + 1
        new ProcessingSequence(
          arr.toList)
          })).toList
    }

    if(row.size >0){
      createList(row);
    }else{
      List[ProcessingSequence]()
    }
  }

  override def selectAll(): List[Component] = {
    Await.result(db.run(components.result),Duration.Inf) match {
      case x=> {
        x.map(y=> {
          Await.result(db.run(componentsOperationMapping.filter(_.componentId === y.id).result),Duration.Inf) match{
            case row:IndexedSeq[ComponentOperationMappingRow] => {
              Component(y.id,y.name,PriorityEnum.NORMAL,createProcessingSequenceList(row.toList))
            }
          }
        }).toList

      }
    }
  }

  override def selectByComponentId(componentId: Int): Option[Component] = {
    Await.result(db.run(components.filter(_.id === componentId).result.headOption),Duration.Inf) match {
      case Some(x)=> {
          Await.result(db.run(componentsOperationMapping.filter(_.componentId === x.id).result),Duration.Inf) match{
            case row => { //:List[ComponentOperationMappingRow]
              Some(Component(x.id, x.name, PriorityEnum.NORMAL, createProcessingSequenceList(row.toList)))
            }
              case _ => None
          }
        }
      }
  }

  //---------------------simulation id based database methods

  override def selectBySimulationId(simulationId: Int): List[Component] = ???
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
