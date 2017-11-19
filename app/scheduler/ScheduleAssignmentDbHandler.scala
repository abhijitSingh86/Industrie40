package scheduler

import db.DbModule
import models._

/**
  * Created by billa on 15.05.17.
  */

trait SchedulerAssignmentHandler{

  def assign(component:Component , operation:Operation , assembly:Assembly):Boolean

}


class ScheduleAssignmentDbHandler(db:DbModule) extends SchedulerAssignmentHandler {


  def assign(cmp:Component,op:Operation,assembly:Assembly):Boolean ={

    if(ComponentQueue.requestQueue.filter(_ == cmp.id).size > 0 ){
        ComponentQueue.requestQueue.remove(cmp.id)
    }

      val newTime = assembly.totalOperations.filter(_.operation.id == op.id)(0).time

      db.updateAssemblyOperationStatus(assembly.id, op.id, BusyOperationStatus.text)

      db.addComponentProcessingInfo(ComponentQueue.getSimulationId(), ComponentQueue.getSimulationVersionId(), cmp.id, assembly.id, cmp.componentSchedulingInfo.sequence,
        op.id, newTime)

  }
}
