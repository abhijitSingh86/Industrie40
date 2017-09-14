package application

import db._
import db.dao._
import network.NetworkProxy
import play.api.ApplicationLoader.Context
import play.api._
import play.api.cache.{EhCacheComponents, EhCacheModule}
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import scheduler.commands.ScheduleCommand
import scheduler.{CompnentSchedulerMunkresAlgo, ComponentScheduler, ScheduleAssignmentDbHandler, SchedulerThread}

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

class MyComponents(context:Context) extends BuiltInComponentsFromContext(context)  with AhcWSComponents with EhCacheComponents   {
  val logger = Logger(this.getClass())

  logger.info("Creating the instantiation graph for Compile time dependency injection")
//  lazy val slickSimulationDao = new SlickSimulationDao with SlickOperationDao with MySqlDBComponent
  lazy val networkProxy = new NetworkProxy(wsClient)
  lazy val dbModule:DbModule = new SlickModuleImplementation(defaultCacheApi) with SlickSimulationDaoRepo with SlickAssemblyDaoRepo
    with SlickComponentDaoRepo with SlickOperationDaoRepo with MySqlDBComponent

  lazy val command = new ScheduleCommand(dbModule,new CompnentSchedulerMunkresAlgo(new ScheduleAssignmentDbHandler(dbModule)),networkProxy)


  lazy val schedulerThread = new SchedulerThread(5000,command)
  logger.info("MyComponent started the Timely scheduler")
  lazy val router = new Routes(httpErrorHandler,schedulingController, applicationController,componentController,simulationCOntrolller, assets)
  lazy val componentController = new controllers.ComponentController(dbModule)
  lazy val applicationController = new controllers.Index(wsClient,dbModule)
//    with SlickOperationDao with MySqlDBComponent
  lazy val schedulingController = new controllers.SchedulingController(schedulerThread,dbModule,networkProxy)

  lazy val simulationCOntrolller = new controllers.SimulationController(dbModule,networkProxy) with SlickSimulationDaoRepo with MySqlDBComponent

  lazy val assets = new controllers.Assets(httpErrorHandler)

}


