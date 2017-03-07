package db.dao

import db.H2DBComponent
import enums.PriorityEnum
import models.{Component, ComponentOperation, ProcessingSequence}
import org.specs2.mutable._
import org.specs2.mutable.Specification
import org.specs2.specification.Step
import org.specs2.specification.core.Fragments

import scalaz.stream.Process.Step

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
  val operationDao:SlickOperationDao  = new SlickOperationDao with H2DBComponent
  val componentDao:ComponentDao = new SlickComponentDAO  with SlickOperationDao with H2DBComponent

  override def beforeAll = {
    //initializeDatabase
    println("*********************in it method**********************")


    def getOp(name:String) = new ComponentOperation(name=name)

    val ids = List(getOp("A"),getOp("B"),getOp("C"),getOp("D")).map(x=> (x.getName() ,
      new ComponentOperation(operationDao.add(x),x.getName()))).toMap

    val seq1 = ProcessingSequence(List(ids("A"),ids("B"),ids("C"),ids("D")))
    val seq2 = ProcessingSequence(List(ids("B"),ids("A"),ids("C"),ids("D")))

    val  component = new Component(0,"someName",PriorityEnum.NORMAL, List(seq1,seq2))
    componentDao.add(component)
  }

  override def afterAll={
    println("*********************destroy method**********************")
    for(x <- componentDao.selectAll()) componentDao.delete(x.id)
    for(x <- operationDao.selectAllOperations()) operationDao.deleteOperation(x.getId())

  }
}
