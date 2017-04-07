package network

import db.dao.SlickSimulationDao
import models.{Assembly, Component, Operation}
import play.api.Logger
import play.api.libs.ws._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 07.01.17.
  */
class NetworkProxy(ws:WSClient) {
  this: SlickSimulationDao =>

  val logger = Logger(this.getClass())
  val componentAssemblyHook = "/assignAssembly"


  def sendAssemblyDetails(url: String, assembly: Assembly, assemblyUrls: Map[Int, String],operation:Operation) = {
      //send http request using assemblies details
    import play.api.libs.json._
    val aurl = assemblyUrls.get(assembly.id).getOrElse("")
    val host = if(aurl.split(":").size >2) aurl.split(":")(1).substring(2) else ""
    val port = if(aurl.split(":").size >2) aurl.split(":")(2).toInt else 0

    val data = Json.obj("assemblyId" -> assembly.id,"assemblyName"->assembly.name
    ,"url" -> url,"transportationTime"->5 , "operationTime"->assembly.totalOperations.filter(_._1.equals(operation)).head._2 ,
    "hostname" -> host, "port" -> port , "operationId" -> operation.id )
    var status=0
    do {
      logger.info(s"Posted Url ${url}${componentAssemblyHook}")
      logger.info(s"Posted Data ${data.toString()}")
      val req = Await.result(ws.url(url+componentAssemblyHook).post(data), Duration.Inf)
      status = if(req.status !=200)status+1 else req.status
    }while (status != 200 && status !=5)
  }

  def sendScheduleInformationToComponent(simulationId: Int, components: List[Component]) = {
    val urls = getAllComponentUrlBySimulationId(simulationId).toMap
    val assemblyUrls =getAllAssemblyUrlBySimulationId(simulationId).toMap
    components.map(x => {
      // attach assembly in simulationAssemblyMapping
      assignAssemblytoComponentSimulationMapping(x.getCurrentAllocatedAssembly().get.id,x.id,simulationId)
      // send request at component attached urls for assembly assignments
      sendAssemblyDetails(urls.get(x.id).get,x.getCurrentAllocatedAssembly().get,assemblyUrls,x.getCurrentOperation().get)
    })
  }

}
