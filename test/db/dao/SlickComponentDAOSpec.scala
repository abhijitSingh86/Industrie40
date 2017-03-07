package db.dao

import db.H2DBComponent
import enums.PriorityEnum
import models.{Component, ComponentOperation, ProcessingSequence}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/**
  * Created by billa on 29.12.16.
  */
@RunWith(classOf[JUnitRunner])
class SlickComponentDAOSpec extends WithDbSpec  {

  "Component Dao" should{
    val obj:ComponentDao = new SlickComponentDAO with H2DBComponent with SlickOperationDao
    "Select all component returns null " in {
        val list = obj.selectAll()
        list.size mustEqual(1)
      }

    "Component should return the newly inserted component" in {
//      val  operationDao  = new SlickOperationDao with H2DBComponent

//      def getOp(name:String) = new ComponentOperation(name=name)
//
//      val ids = List(getOp("A"),getOp("B"),getOp("C"),getOp("D")).map(x=> (x.getName() ,
//        new ComponentOperation(operationDao.add(x),x.getName()))).toMap
//
//
//
//      val seq1 = ProcessingSequence(List(ids("A"),ids("B"),ids("C"),ids("D")))
//      val seq2 = ProcessingSequence(List(ids("B"),ids("A"),ids("C"),ids("D")))
//      val componentDao:ComponentDao = new SlickComponentDAO with H2DBComponent with SlickOperationDao
//      val  component = new Component(0,"someName",PriorityEnum.NORMAL, List(seq1,seq2))
//      componentDao.add(component)

      val list = obj.selectAll()
      list.size mustEqual(1)
//      list(0).processingSequences.contains(ProcessingSequence(List(ids("A"),ids("B"),ids("C"),ids("D")))) mustEqual(true)

      }


  }
}
