package db

import db.dao.{SlickComponentDAO, SlickOperationDao}
import enums.PriorityEnum
import models.{Component, ComponentOperation, ProcessingSequence}
import org.junit.runner.notification.RunNotifier
import org.specs2.runner.JUnitRunner

/**
  * Created by billa on 03.01.17.
  */
class CustomRunner(klass:Class[_]) extends JUnitRunner(klass:Class[_]) with H2DBComponent{

  lazy val initFlag = init()

  def init()={


    true
  }


//  def destroy():Unit ={
//   implement data base clear code
//  }

  override def run(n: RunNotifier): Unit = {
    if(initFlag)
    super.run(n)
  }

}
