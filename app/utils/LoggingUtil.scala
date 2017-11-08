package utils

import play.api.Logger

/**
  * Created by billa on 16.04.17.
  */
object LoggingUtil {


  lazy val accessLog: Logger ={
    Logger("access")
  }


  lazy val fileLog: Logger ={
    Logger("file")
  }

}
