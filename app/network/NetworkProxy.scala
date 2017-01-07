package network

import db.dao.SlickSimulationDao
import models.{Assembly, Component}

/**
  * Created by billa on 07.01.17.
  */
class NetworkProxy {
  this: SlickSimulationDao =>

  def sendAssemblyDetails(url: String, assembly: Assembly, assemblyUrls: Map[Int, String]) = {
      //send http request using assemblies details

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
