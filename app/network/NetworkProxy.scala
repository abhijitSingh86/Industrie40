package network

import models.{Assembly, Operation}
import play.api.Logger
import play.api.libs.ws._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 07.01.17.
  */
class NetworkProxy(ws:WSClient) {

  val logger = Logger(this.getClass())
  val componentAssemblyHook = "/assignAssembly"


  def sendAssemblyDetails(url: String, assembly: Assembly, assemblyUrls: Map[Int, String],operationId:Int) = {
      //send http request using assemblies details
    import play.api.libs.json._
    val aurl = assemblyUrls.get(assembly.id).getOrElse("")
    val host = if(aurl.split(":").size >2) aurl.split(":")(1).substring(2) else ""
    val port = if(aurl.split(":").size >2) aurl.split(":")(2).toInt else 0

    val data = Json.obj("assemblyId" -> assembly.id,"assemblyName"->assembly.name
    ,"url" -> url,"transportationTime"->5 , "operationTime"->assembly.totalOperations.filter(
        _.operation.id.equals(operationId)).head.time ,
    "hostname" -> host, "port" -> port , "operationId" -> operationId )
    var status=0
    do {
      logger.info(s"Posted Url ${url}${componentAssemblyHook}")
      logger.info(s"Posted Data ${data.toString()}")
      val req = Await.result(ws.url(url+componentAssemblyHook).post(data), Duration.Inf)
      status = if(req.status !=200)status+1 else req.status
    }while (status != 200 && status !=5)
  }



}
