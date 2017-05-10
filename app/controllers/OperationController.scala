//package controllers
//
//import db.DBComponent
//import db.dao.OperationDaoRepo
//import models.{Operation, Simulation}
//import play.api.mvc.{Action, AnyContent, Controller, Request}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
///**
//  * Created by billa on 07.04.17.
//  */
//class OperationController extends Controller{
//
//  this: OperationDaoRepo with DBComponent =>
//
//  def addOperation() = Action.async { request =>
//    ApiResponse{
//      for{
//        op <- validationHandler(request)
//        id <- operation.add(op)
//      }yield id
//
//    }
//  }
//
//  def validationHandler(request:Request[AnyContent]):ApiResponse[Operation] = {
//
//    val json = request.body.asJson
//    val name= (json.get \ "operationName").get.as[String]
//
//    ApiResponse.Async.Right(Future.successful(new Operation(id=0,name=name)))
//  }
//
//}
