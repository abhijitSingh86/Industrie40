package network

import javax.inject.Inject

import db.dao.SlickSimulationDao
import play.api.libs.ws._
import models.{Assembly, Component}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 07.01.17.
  */
class NetworkProxy(@Inject ws:WSClient) {
  this: SlickSimulationDao =>

  val componentAssemblyHook = "/assignAssembly"
  def sendAssemblyDetails(url: String, assembly: Assembly, assemblyUrls: Map[Int, String]) = {
      //send http request using assemblies details
    import play.api.libs.json._
    import play.api.libs.functional.syntax._
    val data = Json.obj("assemblyId" -> assembly.id,"assemblyName"->assembly.name
    ,"url" -> assemblyUrls.get(assembly.id))
    var status=0
    do {
      val req = Await.result(ws.url(url+componentAssemblyHook).post(data), Duration.Inf)
      status = if(req.status !=200)status+1 else req.status
    }while (status != 200 || status !=5)


  }

  def sendScheduleInformationToComponent(simulationId: Int, components: List[Component]) = {
    val urls = getAllComponentUrlBySimulationId(simulationId).toMap
    val assemblyUrls =getAllAssemblyUrlBySimulationId(simulationId).toMap
    components.map(x => {
      // attach assembly in simulationAssemblyMapping
      assignAssemblytoComponentSimulationMapping(x.getCurrentAllocatedAssembly().get.id,x.id,simulationId)
      // send request at component attached urls for assembly assignments
      sendAssemblyDetails(urls.get(x.id).get,x.getCurrentAllocatedAssembly().get,assemblyUrls)
    })
  }

}
