package models

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

/**
  * Created by billa on 2016-12-15.
  */
@RunWith(classOf[JUnitRunner])
class OperationSpec extends Specification {

  "Operation" should {
    "return false for equals check with null for reflexivity " in {
        val operation1 = new Operation("operation1",1.1f)
      operation1.equals(null) mustEqual false
    }

    "return false for equals check with None for reflexivity " in {
      val operation1 = new Operation("operation1",1.1f)
      operation1.equals(None) mustEqual false
    }

    "return false for equals check with Any for reflexivity " in {
      val operation1 = new Operation("operation1",1.1f)
      operation1.equals(AnyRef) mustEqual false
    }

    "return true for equals check with valid object for symmetricity" in {
      val operation1 = new Operation("operation1",1.1f)
      val operation2 = new Operation("operation1",1.1f)
      operation1.equals(operation2) mustEqual true
      operation2.equals(operation1) mustEqual true
    }

    "return true for equals check with valid object for transivity" in {
      val operation1 = new Operation("operation1",1.1f)
      val operation2 = new Operation("operation1",1.1f)
      val operation3 = new Operation("operation1",1.1f)
      operation1.equals(operation2) mustEqual true
      operation2.equals(operation3) mustEqual true
      operation1.equals(operation3) mustEqual true
    }

    "return false for equals check with null object with multiple object" in {
      val operation1 = new Operation("operation1",1.1f)
      val operation2 = new Operation("operation1",1.1f)
      operation1.equals(null) mustEqual false
      operation1.equals(null) mustEqual false
      operation1.equals(null) mustEqual false
      operation1.equals(null) mustEqual false
      operation2.equals(null) mustEqual false
    }

  }
}
