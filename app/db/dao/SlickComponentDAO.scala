package db.dao

import dbgeneratedtable.Tables.ComponentOperationMappingRow
import dbgeneratedtable._
import enums.PriorityEnum
import models.{Component, ProcessingSequence}
import slick.driver.MySQLDriver.profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.Duration
/**
  * Created by billa on 2016-12-24.
  */
class SlickComponentDAO(db:Database,operationDao:SlickOperationDao) extends ComponentDao{

  val components = TableQuery[Tables.Component]
  val componentsOperationMapping = TableQuery[Tables.ComponentOperationMapping]

  override def add(component: Component): Int = {

    Await.result(db.run(components returning components.map(obj => obj)+= Tables.ComponentRow(component.id.toInt,component.name)),Duration.Inf) match {
      case dbComponent if dbComponent.id!= null => {
        component.processingSequences.map(l => {
          var seqCounter = 1
          l.seq.map(x => {
            Await.result(db.run(componentsOperationMapping +=ComponentOperationMappingRow(dbComponent.id,x.getId(),seqCounter)),Duration.Inf)
            seqCounter=seqCounter+1
          } )
        })
        dbComponent.id
      }
    }

  }

  override def update(component: Component): Boolean = {
    Await.result(db.run(components.filter(_.id === component.id).update(Tables.ComponentRow(component.id.toInt, component.name))) ,Duration.Inf) match {
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
    val maximumSequenceNum = row.maxBy(x=>x.sequence).sequence
    val listSize = row.size/maximumSequenceNum
    row.sortBy(x=>x.sequence)
    var counter = 0
    (for(_ <-0 until listSize) yield({
      val arr = for(a <- counter until row.size by listSize)yield({
        operationDao.selectByOperationId(row(a).operationId)
      })
      counter = counter+1
      new ProcessingSequence(
          arr.toList)
    })).toList
  }

  override def selectAll(): List[Component] = {
    Await.result(db.run(components.result),Duration.Inf) match {
      case x:List[Tables.ComponentRow] => {
        x.map(y=> {
          Await.result(db.run(componentsOperationMapping.filter(_.componentId === y.id).result),Duration.Inf) match{
            case row:List[ComponentOperationMappingRow] => {
              Component(y.id,y.name,PriorityEnum.NORMAL,createProcessingSequenceList(row))
            }
          }
        })

      }
    }
  }

  override def selectBySimulationId(simulationId: Int): List[Component] = {
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
  }

  override def selectByComponentId(componentId: Int): Option[Component] = {
    Await.result(db.run(components.filter(_.id === componentId).result),Duration.Inf) match {
      case x:Tables.ComponentRow=> {
          Await.result(db.run(componentsOperationMapping.filter(_.componentId === x.id).result),Duration.Inf) match{
            case row:List[ComponentOperationMappingRow] => {
              Some(Component(x.id, x.name, PriorityEnum.NORMAL, createProcessingSequenceList(row)))
            }
              case _ => None
          }
        }
      }
  }
}
