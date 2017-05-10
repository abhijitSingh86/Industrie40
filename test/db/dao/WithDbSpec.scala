package db.dao

import db.H2DBComponent
import enums.PriorityEnum
import models.{Component, Operation, ProcessingSequence}
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragments

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 03.01.17.
  */

trait BeforeAllAfterAll extends Specification {
  override def map(fragments: => Fragments) =
    step(beforeAll) ^ fragments ^ step(afterAll)

  protected def beforeAll()
  protected def afterAll()
}


trait WithDbSpec extends BeforeAllAfterAll with H2DBComponent {//with BeforeEach with AfterAll{
  val operation  = (new SlickOperationDaoRepo  with H2DBComponent).operation
  val componentDao = new SlickComponentDaoRepo   with SlickOperationDaoRepo with H2DBComponent

  override def beforeAll = {
    //initializeDatabase
    println("*********************in it method**********************")


    def getOp(name1:String) = new Operation(0,name1)

    val ids = List(getOp("A"),getOp("B"),getOp("C"),getOp("D"))
              .map(x=> (x.name , operation.add(x)match {
                case id:Int =>  Some(new Operation(id,x.name ))
                case _ => None
                })).toMap

    val seq1 = ProcessingSequence(List(ids("A").get,ids("B").get,ids("C").get,ids("D").get))
    val seq2 = ProcessingSequence(List(ids("B").get,ids("A").get,ids("C").get,ids("D").get))

    val  component = new Component(0,"someName",PriorityEnum.NORMAL, List(seq1,seq2))
    componentDao.component.add(component)
  }

  override def afterAll={
    println("*********************destroy method**********************")
    for(x <- componentDao.component.selectAll()) componentDao.component.delete(x.id)
    for(x <- operation.selectAllOperations()) operation.deleteOperation(x.id)

  }
}
