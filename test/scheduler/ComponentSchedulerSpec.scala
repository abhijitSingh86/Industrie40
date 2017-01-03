package scheduler

import enums.PriorityEnum
import models._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

/**
  * Created by billa on 2016-12-16.
  */
@RunWith(classOf[JUnitRunner])
class ComponentSchedulerSpec extends Specification with BeforeAfter{


  val stepA = new ComponentOperation(name ="A")
  val stepB = new ComponentOperation(name ="B")
  val stepC = new ComponentOperation(name ="C")
  val stepD = new ComponentOperation(name ="D")

  var seq1 = ProcessingSequence(List(stepA,stepB,stepC,stepD))
  var seq2 = ProcessingSequence(List(stepB,stepA,stepC,stepD))
  var seq3 = ProcessingSequence(List(stepA,stepC,stepB,stepD))
  var seq4 = ProcessingSequence(List(stepC,stepA,stepB,stepD))

  var componentA = Component(123123,"myCompA",PriorityEnum.NORMAL,List(seq1,seq2))
  var componentB = Component(123123,"myCompB",PriorityEnum.NORMAL,List(seq3,seq4))

  var assemblyA = Assembly("AssemblyA",List(stepA,stepB,stepC))
  var assemblyB = Assembly("AssemblyB",List(stepD,stepC))

  "Component Scheduler" should {

    "return empty list after first step scheduling for " +
      "compA -> (A,B,C,D),(B,A,C,D)" +
      "compB -> (A,C,B,D),(C,A,B,D)" +
      "AssemblyA ->(A,B,C)" +
      "AssemblyB ->(D,C)" +
      "" in {

      val scheduler:Scheduler = new ComponentScheduler()
      val retList = scheduler.scheduleComponents(List(componentA,componentB),List(assemblyA,assemblyB))
      retList.size mustEqual(0)
      componentA.getCurrentOperation().get.mustEqual(stepB)
      componentB.getCurrentOperation().get.mustEqual(stepC)

      componentA.getCurrentAllocatedAssembly().get mustEqual(assemblyA)
      componentB.getCurrentAllocatedAssembly().get mustEqual(assemblyA)
    }
  }

  "Component scheduler 2" should {
      "return empty list after second step scheduling" in {

        val scheduler:Scheduler = new ComponentScheduler()
              scheduler.scheduleComponents(List(componentA,componentB),List(assemblyA,assemblyB))
        assemblyA.freeOperation(componentA.completeCurrentStep())

        assemblyA.freeOperation(componentB.completeCurrentStep())

        val retList = scheduler.scheduleComponents(List(componentA,componentB),List(assemblyA,assemblyB))
        retList.size mustEqual(1)
        componentA.getCurrentOperation().get.mustEqual(stepA)
        componentB.getCurrentOperation().mustEqual(None)

        componentA.getCurrentAllocatedAssembly().get mustEqual(assemblyA)
        //      componentB.getCurrentAllocatedAssembly().get mustEqual(assemblyA)


      }
  }

  override def before: Any = {
    val stepA = new ComponentOperation(name ="A")
    val stepB = new ComponentOperation(name ="B")
    val stepC = new ComponentOperation(name ="C")
    val stepD = new ComponentOperation(name ="D")

    seq1 = ProcessingSequence(List(stepA,stepB,stepC,stepD))
    seq2 = ProcessingSequence(List(stepB,stepA,stepC,stepD))
    seq3 = ProcessingSequence(List(stepA,stepC,stepB,stepD))
    seq4 = ProcessingSequence(List(stepC,stepA,stepB,stepD))

    componentA = Component(123123,"myCompA",PriorityEnum.NORMAL,List(seq1,seq2))
    componentB = Component(123123,"myCompB",PriorityEnum.NORMAL,List(seq3,seq4))

    assemblyA = Assembly("AssemblyA",List(stepA,stepB,stepC))
    assemblyB = Assembly("AssemblyB",List(stepD,stepC))
  }

  override def after: Any = {}
}
