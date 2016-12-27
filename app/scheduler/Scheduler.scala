package scheduler

import models.{Assembly, Component}

/**
  * Created by billa on 2016-12-16.
  */
trait Scheduler {

  /**
    * Function will take the component and assemblies and schedule them with Interval timing greedy algorithm.
    * Return will be the list of components which are not scheduled by the algorithm this is possible in case of
    * unavailable resources.
    * @param components
    * @param assemblies
    * @return
    */
  def scheduleComponents(components:List[Component],assemblies:List[Assembly]): List[Component]
}
