package controllers

import db.{H2DBComponent, SlickModuleImplementation}
import db.dao.{SlickAssemblyDaoRepo, SlickComponentDaoRepo, SlickOperationDaoRepo, SlickSimulationDaoRepo}
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
        val request = """{"simulationName":"abhi","simulationDesc":"test","operations":[{"id":0,"label":"fixed in Registration omdule"},{"label":"o1","id":1},{"label":"o2","id":2},{"label":"o3","id":3}],"components":[{"id":0,"name":"c1","opCount":3,"operationDetails":[[{"label":"o1","id":1},{"label":"o2","id":2},{"label":"o3","id":3}],[{"label":"o2","id":2},{"label":"o1","id":1},{"label":"o3","id":3}]]},{"id":1,"name":"c2","opCount":2,"operationDetails":[[{"id":0,"label":"fixed in Registration omdule"},{"label":"o1","id":1}],[{"label":"o1","id":1},{"id":0,"label":"fixed in Registration omdule"}]]}],"assemblies":[{"id":0,"name":"a1","operationDetails":[{"time":20,"id":0,"label":"fixed in Registration omdule"},{"time":30,"id":1,"label":"o1"}]},{"id":1,"name":"a2","operationDetails":[{"time":23,"id":1,"label":"o1"},{"time":21,"id":2,"label":"o2"},{"time":20,"id":3,"label":"o3"}]}],"operationCounter":4,"componentCounter":2,"assemblyCounter":2}"""

        val js = Json.parse(request)

        val mod = new SlickModuleImplementation() with SlickSimulationDaoRepo with SlickAssemblyDaoRepo
          with SlickComponentDaoRepo with SlickOperationDaoRepo with H2DBComponent
        val simulationController = new SimulationController(mod)
        val result:Future[Result] = simulationController.addSimulation().apply(FakeRequest().withJsonBody(js))
        val json =contentAsJson(result)

        (json \ "responseType").get.as[String] must be equalTo "successEmpty"
      }
  }
}
