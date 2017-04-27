package filters


import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc._
import utils.LoggingUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by billa on 10.01.17.
  */
class AccessLoggingFilter @Inject() (implicit val mat: Materializer) extends Filter {


  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    val resultFuture = next(request)

    resultFuture.foreach(result => {

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      val msg = s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}" +
        s" status=${result.header.status} requestTime=${requestTime}";
      LoggingUtil.accessLog.info(msg)


    })

    resultFuture
  }
}
