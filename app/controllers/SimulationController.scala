package controllers

import db.DBComponent
import db.dao.SimulationDaoRepo
import models.Simulation
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by billa on 16.04.17.
  */
class SimulationController extends Controller {
  this: SimulationDaoRepo with DBComponent =>


  def addSimulation() = Action.async { request =>
    ApiResponse{
      for{
        simulationObj <- validationHandler(request)
        id <- simulation.add(simulationObj)
      }yield id

    }
  }

//  def updateSimulation() = Action.async{
//    request =>
//
//      ApiResponse{
//        for{
//          simulation <- validationHandler(request)
//          id <- update
//        }
//      }
//  }


  def validationHandler(request:Request[AnyContent]):ApiResponse[Simulation] = {

    val json = request.body.asJson
    val name= (json.get \ "simulationName").get.as[String]
    val desc= (json.get \ "simulationDesc").get.as[String]

    ApiResponse.Async.Right(Future.successful(new Simulation(id=0,name=name,desc = desc)))
  }

}
