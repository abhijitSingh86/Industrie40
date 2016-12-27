package models

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class AssemblySpec extends Specification {
  "Assembly" should {

    "update the allocated operation " in {
      val operation = Operation("operationA",2.3f)
      val assembly  =new Assembly("SomeAssembly", List(operation))
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(operation) must equalTo(true)
    }

    "return false when called with empty total Operations list " in {
      val operation = Operation("operationA",2.3f)
      val assembly  =new Assembly("SomeAssembly")
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(operation) must equalTo(false)
    }

    "return false when called with not existent Operation" in {
      val operation = Operation("operationA",2.3f)
      val operation1 = Operation("operationA",2.2f)
      val assembly  =new Assembly("SomeAssembly",List(operation))
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(operation1) must equalTo(false)
    }

    "return false when called with null Operation" in {
      val operation = Operation("operationA",2.3f)
      val assembly  =new Assembly("SomeAssembly",List(operation))
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(null) must equalTo(false)
    }
  }
}