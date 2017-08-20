package models

/**
  * Created by billa on 03.01.17.
  */
case class Simulation (id:Int,name:String,desc:String,startTime:Long =0,endTime:Long=0,components:List[Component] =List.empty[Component],
                       assemblies: List[Assembly] = List.empty[Assembly]){

}
