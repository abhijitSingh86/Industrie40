package application

import db._
import db.dao._
import network.NetworkProxy
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import scheduler.commands.ScheduleCommand
import scheduler.{ComponentScheduler, ScheduleDbHandler, SchedulerThread}

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

class MyComponents(context:Context) extends BuiltInComponentsFromContext(context)  with AhcWSComponents {
  val logger = Logger(this.getClass())

  logger.info("Creating the instantiation graph for Compile time dependency injection")
//  lazy val slickSimulationDao = new SlickSimulationDao with SlickOperationDao with MySqlDBComponent
  lazy val networkProxy = new NetworkProxy(wsClient)
  lazy val dbModule:DbModule = new SlickModuleImplementation() with SlickSimulationDaoRepo with SlickAssemblyDaoRepo
    with SlickComponentDaoRepo with SlickOperationDaoRepo with MySqlDBComponent

  lazy val command = new ScheduleCommand(dbModule,new ComponentScheduler(new ScheduleDbHandler(dbModule)),networkProxy)


  lazy val schedulerThread = new SchedulerThread(5000,command)
  logger.info("MyComponent started the Timely scheduler")
  lazy val router = new Routes(httpErrorHandler,schedulingController, applicationController,componentController,simulationCOntrolller, assets)
  lazy val componentController = new controllers.ComponentController(dbModule)
  lazy val applicationController = new controllers.Index(wsClient,dbModule)
//    with SlickOperationDao with MySqlDBComponent
  lazy val schedulingController = new controllers.SchedulingController(schedulerThread)

  lazy val simulationCOntrolller = new controllers.SimulationController(dbModule) with SlickSimulationDaoRepo with MySqlDBComponent

  lazy val assets = new controllers.Assets(httpErrorHandler)

//  def reactiveMongoApi: ReactiveMongoApi = new DefaultReactiveMongoApi(configuration, applicationLifecycle)


//  lazy val ws = {
//    import com.typesafe.config.ConfigFactory
//    import play.api._
//    import play.api.libs.ws._
//    import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}
//    import play.api.libs.ws.ahc.AhcConfigBuilder
//    import org.asynchttpclient.AsyncHttpClientConfig
//
//    val configuration = Configuration.reference ++ Configuration(ConfigFactory.parseString(
//      """
//        |ws.followRedirects = true
//      """.stripMargin))
//
//    val parser = new WSConfigParser(configuration, environment)
//    val config = new AhcWSClientConfig(wsClientConfig = parser.parse())
//    val builder = new AhcConfigBuilder(config)
//    val logging = new AsyncHttpClientConfig.AdditionalChannelInitializer() {
//      override def initChannel(channel: io.netty.channel.Channel): Unit = {
//        channel.pipeline.addFirst("log", new io.netty.handler.logging.LoggingHandler("debug"))
//      }
//    }
//    val ahcBuilder = builder.configure()
//    ahcBuilder.setHttpAdditionalChannelInitializer(logging)
//    val ahcConfig = ahcBuilder.build()
//    new AhcWSClient(ahcConfig)
//  }
//
//
//
//  applicationLifecycle.addStopHook(() => Future.successful(ws.close))
}


