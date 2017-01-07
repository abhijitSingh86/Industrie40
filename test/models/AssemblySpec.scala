package models


import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class AssemblySpec extends Specification {
  "Assembly" should {

    "update the allocated operation " in {
      val operation = new AssemblyOperation(0,"operationA",2.3f)
      val assembly  =new Assembly(1,"SomeAssembly", List(operation))
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(operation) must equalTo(true)
    }

    "return false when called with empty total Operations list " in {
      val operation = new AssemblyOperation(0,"operationA",2.3f)
      val assembly  =new Assembly(1,"SomeAssembly")
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(operation) must equalTo(false)
    }

    "return false when called with not existent AssemblyOperation" in {
      val operation = new AssemblyOperation(0,"operationA",2.3f)
      val operation1 = new AssemblyOperation(0,"operationB",2.2f)
      val assembly  =new Assembly(1,"SomeAssembly",List(operation))
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(operation1) must equalTo(false)
    }

    "return false when called with null AssemblyOperation" in {
      val operation = new AssemblyOperation(0,"operationA",2.3f)
      val assembly  =new Assembly(1,"SomeAssembly",List(operation))
      assembly.allocateOperation(operation)
      assembly.allocatedOperations.contains(null) must equalTo(false)
    }
  }
}