package controllers

import db.dao.{SlickAssemblyDaoRepo, SlickComponentDaoRepo, SlickOperationDaoRepo, SlickSimulationDaoRepo}
import db.{H2DBComponent, SlickModuleImplementation}
import play.api.mvc._
import play.api.test._

import scala.concurrent.Future

/**
  * Created by billa on 16.04.17.
  */
class SimulationControllerSpec2 extends PlaySpecification with Results {


    "sim  controller "  should {

        "send simulation object " in {

          val request = FakeRequest(GET,"/simulation/9")
          val mod = new SlickModuleImplementation() with SlickSimulationDaoRepo with SlickAssemblyDaoRepo
            with SlickComponentDaoRepo with SlickOperationDaoRepo with H2DBComponent
          val simulationController = new SimulationController(mod)
          val id =9
         val result:Future[Result] = simulationController.getAllSimulations().apply(request)
//          getSimulation(id).apply(FakeRequest())
          val json =contentAsJson(result)

          println(json)

          (json \ "responseType").get.as[String] must be equalTo "success"
        }
      }




}
