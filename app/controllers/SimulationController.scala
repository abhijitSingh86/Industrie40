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
//      for{
//        simulationObj <- jsonExtractor(request)
//        id <- simulation.add(simulationObj)
//
//      }yield id
      println(request.body.asJson)
      ApiResponse.Async.Right(Future.successful(1))
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


  def jsonExtractor(request:Request[AnyContent]):Future[Simulation] = {

    val json = request.body.asJson

    //Extracting Simulation

    val name= (json.get \ "simulationName").get.as[String]
    val desc= (json.get \ "simulationDesc").get.as[String]

    val simulation = new Simulation(id=0,name=name,desc = desc)
    //Extracting Operations
    


   Future.successful(new Simulation(id=0,name=name,desc = desc))
  }

}
