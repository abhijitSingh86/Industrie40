package models

/**
  * Created by billa on 13.05.17.
  */

abstract class SchedulingInfo{
  def pastProcessings:List[OperationProcessingInfo]
  def currentProcessing:Option[OperationProcessingInfo]
  def sequence:Int
  def completedOperations:List[Operation]
}
case class ComponentSchedulingInfo(pastProcessings:List[OperationProcessingInfo],currentProcessing:Option[OperationProcessingInfo]
                                   ,sequence:Int ,completedOperations:List[Operation] ) extends SchedulingInfo {


}


case object EmptySchedulingInfo  extends SchedulingInfo{

  override def pastProcessings: List[OperationProcessingInfo] = List[OperationProcessingInfo]()

  override def currentProcessing: Option[OperationProcessingInfo] = None

  override def sequence: Int = 1

  override def completedOperations: List[Operation] = List[Operation]()
}
