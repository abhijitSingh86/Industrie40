package scheduler

import db.DbModule
import models.{Assembly, FinishedProcessingStatus, Component, Operation}

/**
  * Created by billa on 15.05.17.
  */
class ScheduleDbHandler(db:DbModule) {


  def assign(cmp:Component,op:Operation,assembly:Assembly) ={

    db.updateAssemblyOperationStatus(assembly.id,op.id,FinishedProcessingStatus.text)

    db.addComponentProcessingInfo(ComponentQueue.getSimulationId(),cmp.id,assembly.id,cmp.componentSchedulingInfo.sequence,
        op.id)
  }
}
