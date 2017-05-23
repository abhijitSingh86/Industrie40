package models

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

/**
  * Created by billa on 17.05.17.
  */
@RunWith(classOf[JUnitRunner])
class ComponentSpec extends Specification{

  "Component Model" should {
    "return correct prosessing sequence" in {

      val ids = Map(
        "A" -> new Operation(1,"A"),
        "B" -> new Operation(2,"B"),
        "C" -> new Operation(3,"C"),
        "D" -> new Operation(4,"D")
      )

      val seq1 = ProcessingSequence(List(ids("A"),ids("B"),ids("C"),ids("D")))
      val seq2 = ProcessingSequence(List(ids("B"),ids("A"),ids("C"),ids("D")))

      val  component = new Component(0,"someName", List(seq1,seq2),EmptySchedulingInfo)
      component.getCurrentProcessingStepOptions().size mustEqual 2
    }
  }

}
