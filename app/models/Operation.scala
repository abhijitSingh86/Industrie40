package models


/**
  * Created by billa on 2016-12-15.
  */

trait Operation{
  def getId():Int
  def getName():String
}


class AssemblyOperation(private val id:Int,private val  name:String,private val time:Float) extends Operation(){
  def getOperationTime():Float =time

  override def getId(): Int = id

  override def getName(): String = name

  /**
    * Check test cases for all condition for equals i.e. consistensy,transivity,reflexive
    * @param obj
    */
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case null => false
      case obj:AssemblyOperation => getName().equalsIgnoreCase(obj.getName()) &&
        getId().compareTo(obj.getId())==0 &&
        getOperationTime().compareTo(obj.getOperationTime()) ==0
      case _ => false
    }
  }
}


class ComponentOperation(private val id:Int,private val  name:String) extends Operation(){


  /**
    * Check test cases for all condition for equals i.e. consistensy,transivity,reflexive
    * @param obj
    */
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case null => false
      case obj:Operation => getName().equalsIgnoreCase(obj.getName()) && getId().compareTo(obj.getId())==0
      case _ => false
    }
  }

  override def getId(): Int = id

  override def getName(): String = name

}
