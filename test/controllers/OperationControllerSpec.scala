//package controllers
//
//import db.H2DBComponent
//import db.dao.SlickOperationDao
//import play.api.libs.json.Json
//import play.api.mvc._
//import play.api.test._
//
//import scala.concurrent.Future
//
///**
//  * Created by billa on 16.04.17.
//  */
//class OperationControllerSpec extends PlaySpecification with Results {
//
//  "Operation controller" should {
//      "send ok" in {
//        val js = Json.obj("operationName"->"test")
//
//        val operationController = new OperationController() with SlickOperationDao with H2DBComponent
//        val result:Future[Result] = operationController.addOperation().apply(FakeRequest().withJsonBody(js))
//        val json =contentAsJson(result)
//        (json \ "status").get.as[String] must be equalTo "ok"
//      }
//  }
//}
