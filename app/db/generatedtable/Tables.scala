package dbgeneratedtable

// AUTO-GENERATED Slick data model
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
  lazy val schema: profile.SchemaDescription = Array(Assembly.schema, AssemblyOperationMapping.schema, AssemblyState.schema, Component.schema, ComponentOperationMapping.schema, ComponentState.schema, Operation.schema, Simulation.schema, Simulationassemblymap.schema, SimulationComponentMapping.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Assembly
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(VARCHAR), Length(255,true) */
  case class AssemblyRow(id: Int, name: String)
  /** GetResult implicit for fetching AssemblyRow objects using plain SQL queries */
  implicit def GetResultAssemblyRow(implicit e0: GR[Int], e1: GR[String]): GR[AssemblyRow] = GR{
    prs => import prs._
      AssemblyRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table Assembly. Objects of this class serve as prototypes for rows in queries. */
  class Assembly(_tableTag: Tag) extends Table[AssemblyRow](_tableTag, "Assembly") {
    def * = (id, name) <> (AssemblyRow.tupled, AssemblyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({r=>import r._; _1.map(_=> AssemblyRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Assembly */
  lazy val Assembly = new TableQuery(tag => new Assembly(tag))

  /** Entity class storing rows of table AssemblyOperationMapping
    *  @param assemblyId Database column assembly_id SqlType(INT)
    *  @param operationId Database column operation_id SqlType(INT)
    *  @param operationTime Database column operation_time SqlType(INT)
    *  @param state Database column state SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param simulationId Database column simulation_id SqlType(INT) */
  case class AssemblyOperationMappingRow(assemblyId: Int, operationId: Int, operationTime: Int, state: Option[String] = None, simulationId: Int)
  /** GetResult implicit for fetching AssemblyOperationMappingRow objects using plain SQL queries */
  implicit def GetResultAssemblyOperationMappingRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[AssemblyOperationMappingRow] = GR{
    prs => import prs._
      AssemblyOperationMappingRow.tupled((<<[Int], <<[Int], <<[Int], <<?[String], <<[Int]))
  }
  /** Table description of table assembly_operation_mapping. Objects of this class serve as prototypes for rows in queries. */
  class AssemblyOperationMapping(_tableTag: Tag) extends Table[AssemblyOperationMappingRow](_tableTag, "assembly_operation_mapping") {
    def * = (assemblyId, operationId, operationTime, state, simulationId) <> (AssemblyOperationMappingRow.tupled, AssemblyOperationMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(assemblyId), Rep.Some(operationId), Rep.Some(operationTime), state, Rep.Some(simulationId)).shaped.<>({r=>import r._; _1.map(_=> AssemblyOperationMappingRow.tupled((_1.get, _2.get, _3.get, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column assembly_id SqlType(INT) */
    val assemblyId: Rep[Int] = column[Int]("assembly_id")
    /** Database column operation_id SqlType(INT) */
    val operationId: Rep[Int] = column[Int]("operation_id")
    /** Database column operation_time SqlType(INT) */
    val operationTime: Rep[Int] = column[Int]("operation_time")
    /** Database column state SqlType(VARCHAR), Length(255,true), Default(None) */
    val state: Rep[Option[String]] = column[Option[String]]("state", O.Length(255,varying=true), O.Default(None))
    /** Database column simulation_id SqlType(INT) */
    val simulationId: Rep[Int] = column[Int]("simulation_id")

    /** Foreign key referencing Assembly (database name fk_assembly_operation_mapping_1) */
    lazy val assemblyFk = foreignKey("fk_assembly_operation_mapping_1", assemblyId, Assembly)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing AssemblyState (database name fk_assembly_operation_mapping_4) */
    lazy val assemblyStateFk = foreignKey("fk_assembly_operation_mapping_4", state, AssemblyState)(r => Rep.Some(r.name), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Operation (database name fk_assembly_operation_mapping_2) */
    lazy val operationFk = foreignKey("fk_assembly_operation_mapping_2", operationId, Operation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Simulation (database name fk_assembly_operation_mapping_3) */
    lazy val simulationFk = foreignKey("fk_assembly_operation_mapping_3", simulationId, Simulation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
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
    *  @param name Database column name SqlType(VARCHAR), Length(255,true) */
  case class ComponentRow(id: Int, name: String)
  /** GetResult implicit for fetching ComponentRow objects using plain SQL queries */
  implicit def GetResultComponentRow(implicit e0: GR[Int], e1: GR[String]): GR[ComponentRow] = GR{
    prs => import prs._
      ComponentRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table Component. Objects of this class serve as prototypes for rows in queries. */
  class Component(_tableTag: Tag) extends Table[ComponentRow](_tableTag, "Component") {
    def * = (id, name) <> (ComponentRow.tupled, ComponentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({r=>import r._; _1.map(_=> ComponentRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
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
    lazy val componentFk = foreignKey("fk_component_operation_mapping_1", componentId, Component)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Operation (database name fk_component_operation_mapping_2) */
    lazy val operationFk = foreignKey("fk_component_operation_mapping_2", operationId, Operation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table ComponentOperationMapping */
  lazy val ComponentOperationMapping = new TableQuery(tag => new ComponentOperationMapping(tag))

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
    *  @param desc Database column desc SqlType(VARCHAR), Length(500,true), Default(None) */
  case class SimulationRow(id: Int, name: String, desc: Option[String] = None)
  /** GetResult implicit for fetching SimulationRow objects using plain SQL queries */
  implicit def GetResultSimulationRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[SimulationRow] = GR{
    prs => import prs._
      SimulationRow.tupled((<<[Int], <<[String], <<?[String]))
  }
  /** Table description of table simulation. Objects of this class serve as prototypes for rows in queries. */
  class Simulation(_tableTag: Tag) extends Table[SimulationRow](_tableTag, "simulation") {
    def * = (id, name, desc) <> (SimulationRow.tupled, SimulationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), desc).shaped.<>({r=>import r._; _1.map(_=> SimulationRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column desc SqlType(VARCHAR), Length(500,true), Default(None) */
    val desc: Rep[Option[String]] = column[Option[String]]("desc", O.Length(500,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Simulation */
  lazy val Simulation = new TableQuery(tag => new Simulation(tag))

  /** Entity class storing rows of table Simulationassemblymap
    *  @param simulationId Database column simulation_id SqlType(INT), PrimaryKey
    *  @param assemblyId Database column assembly_id SqlType(INT) */
  case class SimulationassemblymapRow(simulationId: Int, assemblyId: Int)
  /** GetResult implicit for fetching SimulationassemblymapRow objects using plain SQL queries */
  implicit def GetResultSimulationassemblymapRow(implicit e0: GR[Int]): GR[SimulationassemblymapRow] = GR{
    prs => import prs._
      SimulationassemblymapRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table SimulationAssemblyMap. Objects of this class serve as prototypes for rows in queries. */
  class Simulationassemblymap(_tableTag: Tag) extends Table[SimulationassemblymapRow](_tableTag, "SimulationAssemblyMap") {
    def * = (simulationId, assemblyId) <> (SimulationassemblymapRow.tupled, SimulationassemblymapRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(simulationId), Rep.Some(assemblyId)).shaped.<>({r=>import r._; _1.map(_=> SimulationassemblymapRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column simulation_id SqlType(INT), PrimaryKey */
    val simulationId: Rep[Int] = column[Int]("simulation_id", O.PrimaryKey)
    /** Database column assembly_id SqlType(INT) */
    val assemblyId: Rep[Int] = column[Int]("assembly_id")

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
    *  @param status Database column status SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param assignedassemblyid Database column assignedAssemblyId SqlType(INT), Default(None)
    *  @param completedoperationids Database column completedOperationIds SqlType(VARCHAR), Length(500,true), Default(None)
    *  @param currentoperationid Database column currentOperationId SqlType(INT), Default(None) */
  case class SimulationComponentMappingRow(simulationId: Int, componentId: Int, status: Option[String] = None, assignedassemblyid: Option[Int] = None, completedoperationids: Option[String] = None, currentoperationid: Option[Int] = None)
  /** GetResult implicit for fetching SimulationComponentMappingRow objects using plain SQL queries */
  implicit def GetResultSimulationComponentMappingRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[SimulationComponentMappingRow] = GR{
    prs => import prs._
      SimulationComponentMappingRow.tupled((<<[Int], <<[Int], <<?[String], <<?[Int], <<?[String], <<?[Int]))
  }
  /** Table description of table simulation_component_mapping. Objects of this class serve as prototypes for rows in queries. */
  class SimulationComponentMapping(_tableTag: Tag) extends Table[SimulationComponentMappingRow](_tableTag, "simulation_component_mapping") {
    def * = (simulationId, componentId, status, assignedassemblyid, completedoperationids, currentoperationid) <> (SimulationComponentMappingRow.tupled, SimulationComponentMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(simulationId), Rep.Some(componentId), status, assignedassemblyid, completedoperationids, currentoperationid).shaped.<>({r=>import r._; _1.map(_=> SimulationComponentMappingRow.tupled((_1.get, _2.get, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column simulation_id SqlType(INT) */
    val simulationId: Rep[Int] = column[Int]("simulation_id")
    /** Database column component_id SqlType(INT) */
    val componentId: Rep[Int] = column[Int]("component_id")
    /** Database column status SqlType(VARCHAR), Length(255,true), Default(None) */
    val status: Rep[Option[String]] = column[Option[String]]("status", O.Length(255,varying=true), O.Default(None))
    /** Database column assignedAssemblyId SqlType(INT), Default(None) */
    val assignedassemblyid: Rep[Option[Int]] = column[Option[Int]]("assignedAssemblyId", O.Default(None))
    /** Database column completedOperationIds SqlType(VARCHAR), Length(500,true), Default(None) */
    val completedoperationids: Rep[Option[String]] = column[Option[String]]("completedOperationIds", O.Length(500,varying=true), O.Default(None))
    /** Database column currentOperationId SqlType(INT), Default(None) */
    val currentoperationid: Rep[Option[Int]] = column[Option[Int]]("currentOperationId", O.Default(None))

    /** Foreign key referencing Component (database name fk_simulation_component_mapping_1) */
    lazy val componentFk = foreignKey("fk_simulation_component_mapping_1", componentId, Component)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing ComponentState (database name fk_simulation_component_mapping_3) */
    lazy val componentStateFk = foreignKey("fk_simulation_component_mapping_3", status, ComponentState)(r => Rep.Some(r.name), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Simulation (database name fk_simulation_component_mapping_2) */
    lazy val simulationFk = foreignKey("fk_simulation_component_mapping_2", simulationId, Simulation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SimulationComponentMapping */
  lazy val SimulationComponentMapping = new TableQuery(tag => new SimulationComponentMapping(tag))
}
