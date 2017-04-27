package controllers

import db.H2DBComponent
import db.dao.SlickSimulationDaoRepo
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test._

import scala.concurrent.Future

/**
  * Created by billa on 16.04.17.
  */
class SimulationControllerSpec extends PlaySpecification with Results {

  "Simulation controller" should {
      "send ok" in {
        val request = """{"simulationName":"test1","simulationDesc":"testing description" }"""
        val js = Json.obj("simulationName"->"test","simulationDesc"->"Description")

        val simulationController = new SimulationController() with SlickSimulationDaoRepo with H2DBComponent
        val result:Future[Result] = simulationController.addSimulation().apply(FakeRequest().withJsonBody(js))
        val json =contentAsJson(result)
        (json \ "status").get.as[String] must be equalTo "ok"
      }
  }
}
