package network

import models.{Assembly, Operation}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc.Results

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by billa on 07.01.17.
  */
class NetworkProxy(ws:WSClient) {

  val logger = Logger(this.getClass())
  val componentAssemblyHook = "/assignAssembly"
  val componentStartSimulationHook = "/startScheduling"
  val assemblyFailureHook = "/receiveFailure"


  def sendAssemblyDetails(url: String, assembly: Assembly, assemblyUrls: Map[Int, String],operationId:Int,transportTime:Int) = {
      //send http request using assemblies details
    import play.api.libs.json._
    val aurl = assemblyUrls.get(assembly.id).getOrElse("")
    val host = if(aurl.split(":").size >2) aurl.split(":")(1).substring(2) else ""
    val port = if(aurl.split(":").size >2) aurl.split(":")(2).toInt else 0
    val data = Json.obj("assemblyId" -> assembly.id,"assemblyName"->assembly.name
    ,"url" -> url,"transportationTime"->transportTime, "operationTime"->assembly.totalOperations.filter(
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

  def sendFailureNotificationToAssembly(url:String,failureTime:Int,action:String):Boolean={

    var status=0
    var flag  =true
    do{
      val data = Json.obj("failureTime"->failureTime,"componentAction"->action)
      val req = Await.result(ws.url(url+assemblyFailureHook).post(data),Duration.Inf)
      logger.info("Assembly Failure Request send and Response is "+req.status+"  :: "+req)

      status = if(req.status !=200)status+1 else req.status
      flag = ((Json.parse(req.body.toString) \ "body") \ "status").as[Boolean]
    }while(status !=200 && status !=5)
    flag
  }
  def sendSimulationStartDetails(url: String) = {
    //send http request using assemblies details
    val host = if(url.split(":").size >2) url.split(":")(1).substring(2) else ""
    val port = if(url.split(":").size >2) url.split(":")(2).toInt else 0


    var status=0
    do {
      logger.info(s"Posted Url ${url}${componentStartSimulationHook}")
      val req = Await.result(ws.url(url+componentStartSimulationHook).post(Results.EmptyContent()), Duration.Inf)
      status = if(req.status !=200)status+1 else req.status
    }while (status != 200 && status !=5)
  }



}
