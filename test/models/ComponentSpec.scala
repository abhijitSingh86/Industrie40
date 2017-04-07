package models

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import enums.PriorityEnum
/**
  * Created by billa on 2016-12-15.
  */
@RunWith(classOf[JUnitRunner])
class ComponentSpec extends Specification{

  "Component" should{
    "return available processing step options for step 0" in {
      val stepA = new Operation(1,"A")
      val stepB = new Operation(1,"B")
      val stepC = new Operation(1,"C")
      val stepD = new Operation(1,"D")

      val seq1 = ProcessingSequence(List(stepA,stepB,stepC,stepD))
      val seq2 = ProcessingSequence(List(stepC,stepD,stepA,stepB))

      val component = Component(123123,"myComp",PriorityEnum.NORMAL,List(seq1,seq2))

      component.getCurrentProcessingStepOptions().size mustEqual(2)
      component.getCurrentProcessingStepOptions()(0) mustEqual(stepA)
      component.getCurrentProcessingStepOptions()(1) mustEqual(stepC)
    }

    "return available processing step options for valid stepCounter i.e. 2" in {
      val stepA = new Operation(1,"A")
      val stepB = new Operation(1,"B")
      val stepC = new Operation(1,"C")
      val stepD = new Operation(1,"D")

      val seq1 = ProcessingSequence(List(stepA,stepB,stepC,stepD))
      val seq2 = ProcessingSequence(List(stepB,stepA,stepC,stepD))

      val component = Component(123123,"myComp",PriorityEnum.NORMAL,List(seq1,seq2))
      val operations = component.getCurrentProcessingStepOptions()
      //scheduler scheduled some operation and assembly
      component.scheduleCurrentOperation(operations(0), new Assembly(1,"someAssembly"))
      //assembly marked complete the assembly
      component.completeCurrentStep()

      // now it is retrieving for next steps
      component.getCurrentProcessingStepOptions().size mustEqual(1)
      component.getCurrentProcessingStepOptions()(0) mustEqual(stepB)

    }

    "return available processing step options for valid stepCounter i.e. 2" in {
      val stepA = new Operation(1,"A")
      val stepB = new Operation(1,"B")
      val stepC = new Operation(1,"C")
      val stepD = new Operation(1,"D")

      val seq1 = ProcessingSequence(List(stepA,stepB,stepC,stepD))
      val seq2 = ProcessingSequence(List(stepB,stepA,stepC,stepD))
      val seq3 = ProcessingSequence(List(stepA,stepC,stepB,stepD))
      val seq4 = ProcessingSequence(List(stepC,stepA,stepB,stepD))

      val component = Component(123123,"myComp",PriorityEnum.NORMAL,List(seq1,seq2,seq3,seq4))
      val operations = component.getCurrentProcessingStepOptions()
      //scheduler scheduled some operation and assembly
      component.scheduleCurrentOperation(operations(0), new Assembly(0,"someAssembly"))
      //assembly marked complete the assembly
      component.completeCurrentStep()

      // now it is retrieving for next steps
      component.getCurrentProcessingStepOptions().size mustEqual(2)
      component.getCurrentProcessingStepOptions()(0) mustEqual(stepB)
      component.getCurrentProcessingStepOptions()(1) mustEqual(stepC)

    }

    "return available processing step options for valid stepCounter i.e. 2" in {
      val stepA = new Operation(1,"A")
      val stepB = new Operation(1,"B")
      val stepC = new Operation(1,"C")
      val stepD = new Operation(1,"D")

      val seq1 = ProcessingSequence(List(stepA,stepB,stepC,stepD))
      val seq2 = ProcessingSequence(List(stepB,stepA,stepC,stepD))
      val seq3 = ProcessingSequence(List(stepA,stepC,stepB,stepD))
      val seq4 = ProcessingSequence(List(stepC,stepA,stepB,stepD))

      val component = Component(123123,"myComp",PriorityEnum.NORMAL,List(seq1,seq2,seq3,seq4))
      val operations = component.getCurrentProcessingStepOptions()
      //scheduler scheduled some operation and assembly
      component.scheduleCurrentOperation(operations(1), new Assembly(1,"someAssembly"))
      //assembly marked complete the assembly
      component.completeCurrentStep()

      // now it is retrieving for next steps
      component.getCurrentProcessingStepOptions().size mustEqual(1)
      component.getCurrentProcessingStepOptions()(0) mustEqual(stepA)

    }

  }

}
