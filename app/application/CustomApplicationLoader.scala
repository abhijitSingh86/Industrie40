package application

import play.api.ApplicationLoader.Context
import play.api._
import router.Routes

import scala.concurrent.Future

/**
  * Created by billa on 10.01.17.
  */
class CustomApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new MyComponents(context).application
  }
}

class MyComponents(context:Context) extends BuiltInComponentsFromContext(context){
  val logger = Logger(this.getClass())

  logger.info("MyComponent class in initializing -- about to start the Timely scheduler")

  logger.info("MyComponent started the Timely scheduler")
  lazy val router = new Routes(httpErrorHandler, applicationController,componentController, assets)
  lazy val componentController = new controllers.ComponentController
  lazy val applicationController = new controllers.Index(ws)
  lazy val assets = new controllers.Assets(httpErrorHandler)


  lazy val ws = {
    import com.typesafe.config.ConfigFactory
    import play.api._
    import play.api.libs.ws._
    import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}
    import play.api.libs.ws.ahc.AhcConfigBuilder
    import org.asynchttpclient.AsyncHttpClientConfig

    val configuration = Configuration.reference ++ Configuration(ConfigFactory.parseString(
      """
        |ws.followRedirects = true
      """.stripMargin))

    val parser = new WSConfigParser(configuration, environment)
    val config = new AhcWSClientConfig(wsClientConfig = parser.parse())
    val builder = new AhcConfigBuilder(config)
    val logging = new AsyncHttpClientConfig.AdditionalChannelInitializer() {
      override def initChannel(channel: io.netty.channel.Channel): Unit = {
        channel.pipeline.addFirst("log", new io.netty.handler.logging.LoggingHandler("debug"))
      }
    }
    val ahcBuilder = builder.configure()
    ahcBuilder.setHttpAdditionalChannelInitializer(logging)
    val ahcConfig = ahcBuilder.build()
    new AhcWSClient(ahcConfig)
  }
  applicationLifecycle.addStopHook(() => Future.successful(ws.close))
}


