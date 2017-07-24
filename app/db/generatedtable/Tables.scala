package db.generatedtable
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Assembly.schema, AssemblyOperationMapping.schema, AssemblyState.schema, Component.schema, ComponentOperationMapping.schema, ComponentProcessingState.schema, ComponentState.schema, Operation.schema, Simulation.schema, Simulationassemblymap.schema, SimulationComponentMapping.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Assembly
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(VARCHAR), Length(255,true)
    *  @param lastactive Database column lastActive SqlType(DATETIME), Default(None)
    *  @param failurenumber Database column failureNumber SqlType(INT), Default(Some(0))
    *  @param failuretime Database column failureTime SqlType(INT), Default(Some(0)) */
  case class AssemblyRow(id: Int, name: String, lastactive: Option[java.sql.Timestamp] = None, failurenumber: Option[Int] = Some(0), failuretime: Option[Int] = Some(0))
  /** GetResult implicit for fetching AssemblyRow objects using plain SQL queries */
  implicit def GetResultAssemblyRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Int]]): GR[AssemblyRow] = GR{
    prs => import prs._
      AssemblyRow.tupled((<<[Int], <<[String], <<?[java.sql.Timestamp], <<?[Int], <<?[Int]))
  }
  /** Table description of table Assembly. Objects of this class serve as prototypes for rows in queries. */
  class Assembly(_tableTag: Tag) extends Table[AssemblyRow](_tableTag, "Assembly") {
    def * = (id, name, lastactive, failurenumber, failuretime) <> (AssemblyRow.tupled, AssemblyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), lastactive, failurenumber, failuretime).shaped.<>({r=>import r._; _1.map(_=> AssemblyRow.tupled((_1.get, _2.get, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column lastActive SqlType(DATETIME), Default(None) */
    val lastactive: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastActive", O.Default(None))
    /** Database column failureNumber SqlType(INT), Default(Some(0)) */
    val failurenumber: Rep[Option[Int]] = column[Option[Int]]("failureNumber", O.Default(Some(0)))
    /** Database column failureTime SqlType(INT), Default(Some(0)) */
    val failuretime: Rep[Option[Int]] = column[Option[Int]]("failureTime", O.Default(Some(0)))
  }
  /** Collection-like TableQuery object for table Assembly */
  lazy val Assembly = new TableQuery(tag => new Assembly(tag))

  /** Entity class storing rows of table AssemblyOperationMapping
    *  @param assemblyId Database column assembly_id SqlType(INT)
    *  @param operationId Database column operation_id SqlType(INT)
    *  @param operationTime Database column operation_time SqlType(INT)
    *  @param status Database column status SqlType(VARCHAR), Length(255,true) */
  case class AssemblyOperationMappingRow(assemblyId: Int, operationId: Int, operationTime: Int, status: String)
  /** GetResult implicit for fetching AssemblyOperationMappingRow objects using plain SQL queries */
  implicit def GetResultAssemblyOperationMappingRow(implicit e0: GR[Int], e1: GR[String]): GR[AssemblyOperationMappingRow] = GR{
    prs => import prs._
      AssemblyOperationMappingRow.tupled((<<[Int], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table assembly_operation_mapping. Objects of this class serve as prototypes for rows in queries. */
  class AssemblyOperationMapping(_tableTag: Tag) extends Table[AssemblyOperationMappingRow](_tableTag, "assembly_operation_mapping") {
    def * = (assemblyId, operationId, operationTime, status) <> (AssemblyOperationMappingRow.tupled, AssemblyOperationMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(assemblyId), Rep.Some(operationId), Rep.Some(operationTime), Rep.Some(status)).shaped.<>({r=>import r._; _1.map(_=> AssemblyOperationMappingRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column assembly_id SqlType(INT) */
    val assemblyId: Rep[Int] = column[Int]("assembly_id")
    /** Database column operation_id SqlType(INT) */
    val operationId: Rep[Int] = column[Int]("operation_id")
    /** Database column operation_time SqlType(INT) */
    val operationTime: Rep[Int] = column[Int]("operation_time")
    /** Database column status SqlType(VARCHAR), Length(255,true) */
    val status: Rep[String] = column[String]("status", O.Length(255,varying=true))

    /** Foreign key referencing Assembly (database name fk_assembly_operation_mapping_1) */
    lazy val assemblyFk = foreignKey("fk_assembly_operation_mapping_1", assemblyId, Assembly)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Operation (database name fk_assembly_operation_mapping_2) */
    lazy val operationFk = foreignKey("fk_assembly_operation_mapping_2", operationId, Operation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table AssemblyOperationMapping */
  lazy val AssemblyOperationMapping = new TableQuery(tag => new AssemblyOperationMapping(tag))

  /** Entity class storing rows of table AssemblyState
    *  @param name Database column name SqlType(VARCHAR), PrimaryKey, Length(255,true) */
  case class AssemblyStateRow(name: String)
  /** GetResult implicit for fetching AssemblyStateRow objects using plain SQL queries */
  implicit def GetResultAssemblyStateRow(implicit e0: GR[String]): GR[AssemblyStateRow] = GR{
    prs => import prs._
      AssemblyStateRow(<<[String])
  }
  /** Table description of table Assembly_state. Objects of this class serve as prototypes for rows in queries. */
  class AssemblyState(_tableTag: Tag) extends Table[AssemblyStateRow](_tableTag, "Assembly_state") {
    def * = name <> (AssemblyStateRow, AssemblyStateRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(name).shaped.<>(r => r.map(_=> AssemblyStateRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column name SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val name: Rep[String] = column[String]("name", O.PrimaryKey, O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table AssemblyState */
  lazy val AssemblyState = new TableQuery(tag => new AssemblyState(tag))

  /** Entity class storing rows of table Component
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(VARCHAR), Length(255,true)
    *  @param lastActive Database column last_active SqlType(DATETIME), Default(None) */
  case class ComponentRow(id: Int, name: String, lastActive: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching ComponentRow objects using plain SQL queries */
  implicit def GetResultComponentRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[java.sql.Timestamp]]): GR[ComponentRow] = GR{
    prs => import prs._
      ComponentRow.tupled((<<[Int], <<[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table Component. Objects of this class serve as prototypes for rows in queries. */
  class Component(_tableTag: Tag) extends Table[ComponentRow](_tableTag, "Component") {
    def * = (id, name, lastActive) <> (ComponentRow.tupled, ComponentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), lastActive).shaped.<>({r=>import r._; _1.map(_=> ComponentRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column last_active SqlType(DATETIME), Default(None) */
    val lastActive: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("last_active", O.Default(None))
  }
  /** Collection-like TableQuery object for table Component */
  lazy val Component = new TableQuery(tag => new Component(tag))

  /** Entity class storing rows of table ComponentOperationMapping
    *  @param componentId Database column component_id SqlType(INT)
    *  @param operationId Database column operation_id SqlType(INT)
    *  @param sequence Database column sequence SqlType(INT) */
  case class ComponentOperationMappingRow(componentId: Int, operationId: Int, sequence: Int)
  /** GetResult implicit for fetching ComponentOperationMappingRow objects using plain SQL queries */
  implicit def GetResultComponentOperationMappingRow(implicit e0: GR[Int]): GR[ComponentOperationMappingRow] = GR{
    prs => import prs._
      ComponentOperationMappingRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table component_operation_mapping. Objects of this class serve as prototypes for rows in queries. */
  class ComponentOperationMapping(_tableTag: Tag) extends Table[ComponentOperationMappingRow](_tableTag, "component_operation_mapping") {
    def * = (componentId, operationId, sequence) <> (ComponentOperationMappingRow.tupled, ComponentOperationMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(componentId), Rep.Some(operationId), Rep.Some(sequence)).shaped.<>({r=>import r._; _1.map(_=> ComponentOperationMappingRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column component_id SqlType(INT) */
    val componentId: Rep[Int] = column[Int]("component_id")
    /** Database column operation_id SqlType(INT) */
    val operationId: Rep[Int] = column[Int]("operation_id")
    /** Database column sequence SqlType(INT) */
    val sequence: Rep[Int] = column[Int]("sequence")

    /** Foreign key referencing Component (database name fk_component_operation_mapping_1) */
    lazy val componentFk = foreignKey("fk_component_operation_mapping_1", componentId, Component)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Operation (database name fk_component_operation_mapping_2) */
    lazy val operationFk = foreignKey("fk_component_operation_mapping_2", operationId, Operation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ComponentOperationMapping */
  lazy val ComponentOperationMapping = new TableQuery(tag => new ComponentOperationMapping(tag))

  /** Entity class storing rows of table ComponentProcessingState
    *  @param componentid Database column componentId SqlType(INT)
    *  @param simulationid Database column simulationId SqlType(INT)
    *  @param sequencenum Database column sequenceNum SqlType(INT)
    *  @param operationid Database column operationId SqlType(INT)
    *  @param startTime Database column start_time SqlType(DATETIME), Default(None)
    *  @param endTime Database column end_time SqlType(DATETIME), Default(None)
    *  @param assemblyid Database column assemblyId SqlType(INT)
    *  @param status Database column status SqlType(VARCHAR), Length(45,true)
    *  @param failwaittime Database column failWaitTime SqlType(INT), Default(None) */
  case class ComponentProcessingStateRow(componentid: Int, simulationid: Int, sequencenum: Int, operationid: Int, startTime: Option[java.sql.Timestamp] = None, endTime: Option[java.sql.Timestamp] = None, assemblyid: Int, status: String, failwaittime: Option[Int] = None)
  /** GetResult implicit for fetching ComponentProcessingStateRow objects using plain SQL queries */
  implicit def GetResultComponentProcessingStateRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Timestamp]], e2: GR[String], e3: GR[Option[Int]]): GR[ComponentProcessingStateRow] = GR{
    prs => import prs._
      ComponentProcessingStateRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<[Int], <<[String], <<?[Int]))
  }
  /** Table description of table component_processing_state. Objects of this class serve as prototypes for rows in queries. */
  class ComponentProcessingState(_tableTag: Tag) extends Table[ComponentProcessingStateRow](_tableTag, "component_processing_state") {
    def * = (componentid, simulationid, sequencenum, operationid, startTime, endTime, assemblyid, status, failwaittime) <> (ComponentProcessingStateRow.tupled, ComponentProcessingStateRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(componentid), Rep.Some(simulationid), Rep.Some(sequencenum), Rep.Some(operationid), startTime, endTime, Rep.Some(assemblyid), Rep.Some(status), failwaittime).shaped.<>({r=>import r._; _1.map(_=> ComponentProcessingStateRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7.get, _8.get, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column componentId SqlType(INT) */
    val componentid: Rep[Int] = column[Int]("componentId")
    /** Database column simulationId SqlType(INT) */
    val simulationid: Rep[Int] = column[Int]("simulationId")
    /** Database column sequenceNum SqlType(INT) */
    val sequencenum: Rep[Int] = column[Int]("sequenceNum")
    /** Database column operationId SqlType(INT) */
    val operationid: Rep[Int] = column[Int]("operationId")
    /** Database column start_time SqlType(DATETIME), Default(None) */
    val startTime: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("start_time", O.Default(None))
    /** Database column end_time SqlType(DATETIME), Default(None) */
    val endTime: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("end_time", O.Default(None))
    /** Database column assemblyId SqlType(INT) */
    val assemblyid: Rep[Int] = column[Int]("assemblyId")
    /** Database column status SqlType(VARCHAR), Length(45,true) */
    val status: Rep[String] = column[String]("status", O.Length(45,varying=true))
    /** Database column failWaitTime SqlType(INT), Default(None) */
    val failwaittime: Rep[Option[Int]] = column[Option[Int]]("failWaitTime", O.Default(None))

    /** Primary key of ComponentProcessingState (database name component_processing_state_PK) */
    val pk = primaryKey("component_processing_state_PK", (componentid, simulationid, assemblyid, operationid, sequencenum,startTime))
  }
  /** Collection-like TableQuery object for table ComponentProcessingState */
  lazy val ComponentProcessingState = new TableQuery(tag => new ComponentProcessingState(tag))

  /** Entity class storing rows of table ComponentState
    *  @param name Database column name SqlType(VARCHAR), PrimaryKey, Length(255,true) */
  case class ComponentStateRow(name: String)
  /** GetResult implicit for fetching ComponentStateRow objects using plain SQL queries */
  implicit def GetResultComponentStateRow(implicit e0: GR[String]): GR[ComponentStateRow] = GR{
    prs => import prs._
      ComponentStateRow(<<[String])
  }
  /** Table description of table component_state. Objects of this class serve as prototypes for rows in queries. */
  class ComponentState(_tableTag: Tag) extends Table[ComponentStateRow](_tableTag, "component_state") {
    def * = name <> (ComponentStateRow, ComponentStateRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(name).shaped.<>(r => r.map(_=> ComponentStateRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column name SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val name: Rep[String] = column[String]("name", O.PrimaryKey, O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table ComponentState */
  lazy val ComponentState = new TableQuery(tag => new ComponentState(tag))

  /** Entity class storing rows of table Operation
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(VARCHAR), Length(255,true) */
  case class OperationRow(id: Int, name: String)
  /** GetResult implicit for fetching OperationRow objects using plain SQL queries */
  implicit def GetResultOperationRow(implicit e0: GR[Int], e1: GR[String]): GR[OperationRow] = GR{
    prs => import prs._
      OperationRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table Operation. Objects of this class serve as prototypes for rows in queries. */
  class Operation(_tableTag: Tag) extends Table[OperationRow](_tableTag, "Operation") {
    def * = (id, name) <> (OperationRow.tupled, OperationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({r=>import r._; _1.map(_=> OperationRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Operation */
  lazy val Operation = new TableQuery(tag => new Operation(tag))

  /** Entity class storing rows of table Simulation
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(VARCHAR), Length(255,true)
    *  @param desc Database column desc SqlType(VARCHAR), Length(500,true), Default(None)
    *  @param starttime Database column starttime SqlType(BIGINT), Default(None)
    *  @param endtime Database column endtime SqlType(BIGINT), Default(None) */
  case class SimulationRow(id: Int, name: String, desc: Option[String] = None, starttime: Option[Long] = None, endtime: Option[Long] = None)
  /** GetResult implicit for fetching SimulationRow objects using plain SQL queries */
  implicit def GetResultSimulationRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[SimulationRow] = GR{
    prs => import prs._
      SimulationRow.tupled((<<[Int], <<[String], <<?[String], <<?[Long], <<?[Long]))
  }
  /** Table description of table simulation. Objects of this class serve as prototypes for rows in queries. */
  class Simulation(_tableTag: Tag) extends Table[SimulationRow](_tableTag, "simulation") {
    def * = (id, name, desc, starttime, endtime) <> (SimulationRow.tupled, SimulationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), desc, starttime, endtime).shaped.<>({r=>import r._; _1.map(_=> SimulationRow.tupled((_1.get, _2.get, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column desc SqlType(VARCHAR), Length(500,true), Default(None) */
    val desc: Rep[Option[String]] = column[Option[String]]("desc", O.Length(500,varying=true), O.Default(None))
    /** Database column starttime SqlType(BIGINT), Default(None) */
    val starttime: Rep[Option[Long]] = column[Option[Long]]("starttime", O.Default(None))
    /** Database column endtime SqlType(BIGINT), Default(None) */
    val endtime: Rep[Option[Long]] = column[Option[Long]]("endtime", O.Default(None))
  }
  /** Collection-like TableQuery object for table Simulation */
  lazy val Simulation = new TableQuery(tag => new Simulation(tag))

  /** Entity class storing rows of table Simulationassemblymap
    *  @param simulationId Database column simulation_id SqlType(INT)
    *  @param assemblyId Database column assembly_id SqlType(INT)
    *  @param url Database column url SqlType(VARCHAR), Length(255,true), Default(None) */
  case class SimulationassemblymapRow(simulationId: Int, assemblyId: Int, url: Option[String] = None)
  /** GetResult implicit for fetching SimulationassemblymapRow objects using plain SQL queries */
  implicit def GetResultSimulationassemblymapRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[SimulationassemblymapRow] = GR{
    prs => import prs._
      SimulationassemblymapRow.tupled((<<[Int], <<[Int], <<?[String]))
  }
  /** Table description of table SimulationAssemblyMap. Objects of this class serve as prototypes for rows in queries. */
  class Simulationassemblymap(_tableTag: Tag) extends Table[SimulationassemblymapRow](_tableTag, "SimulationAssemblyMap") {
    def * = (simulationId, assemblyId, url) <> (SimulationassemblymapRow.tupled, SimulationassemblymapRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(simulationId), Rep.Some(assemblyId), url).shaped.<>({r=>import r._; _1.map(_=> SimulationassemblymapRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column simulation_id SqlType(INT) */
    val simulationId: Rep[Int] = column[Int]("simulation_id")
    /** Database column assembly_id SqlType(INT) */
    val assemblyId: Rep[Int] = column[Int]("assembly_id")
    /** Database column url SqlType(VARCHAR), Length(255,true), Default(None) */
    val url: Rep[Option[String]] = column[Option[String]]("url", O.Length(255,varying=true), O.Default(None))

    /** Foreign key referencing Assembly (database name fk_SimulationAssemblyMap_1) */
    lazy val assemblyFk = foreignKey("fk_SimulationAssemblyMap_1", assemblyId, Assembly)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Simulation (database name fk_SimulationAssemblyMap_2) */
    lazy val simulationFk = foreignKey("fk_SimulationAssemblyMap_2", simulationId, Simulation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Simulationassemblymap */
  lazy val Simulationassemblymap = new TableQuery(tag => new Simulationassemblymap(tag))

  /** Entity class storing rows of table SimulationComponentMapping
    *  @param simulationId Database column simulation_id SqlType(INT)
    *  @param componentId Database column component_id SqlType(INT)
    *  @param url Database column url SqlType(VARCHAR), Length(255,true), Default(None) */
  case class SimulationComponentMappingRow(simulationId: Int, componentId: Int, url: Option[String] = None)
  /** GetResult implicit for fetching SimulationComponentMappingRow objects using plain SQL queries */
  implicit def GetResultSimulationComponentMappingRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[SimulationComponentMappingRow] = GR{
    prs => import prs._
      SimulationComponentMappingRow.tupled((<<[Int], <<[Int], <<?[String]))
  }
  /** Table description of table simulation_component_mapping. Objects of this class serve as prototypes for rows in queries. */
  class SimulationComponentMapping(_tableTag: Tag) extends Table[SimulationComponentMappingRow](_tableTag, "simulation_component_mapping") {
    def * = (simulationId, componentId, url) <> (SimulationComponentMappingRow.tupled, SimulationComponentMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(simulationId), Rep.Some(componentId), url).shaped.<>({r=>import r._; _1.map(_=> SimulationComponentMappingRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column simulation_id SqlType(INT) */
    val simulationId: Rep[Int] = column[Int]("simulation_id")
    /** Database column component_id SqlType(INT) */
    val componentId: Rep[Int] = column[Int]("component_id")
    /** Database column url SqlType(VARCHAR), Length(255,true), Default(None) */
    val url: Rep[Option[String]] = column[Option[String]]("url", O.Length(255,varying=true), O.Default(None))

    /** Foreign key referencing Component (database name fk_simulation_component_mapping_1) */
    lazy val componentFk = foreignKey("fk_simulation_component_mapping_1", componentId, Component)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Simulation (database name fk_simulation_component_mapping_2) */
    lazy val simulationFk = foreignKey("fk_simulation_component_mapping_2", simulationId, Simulation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SimulationComponentMapping */
  lazy val SimulationComponentMapping = new TableQuery(tag => new SimulationComponentMapping(tag))
}
