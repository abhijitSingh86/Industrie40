package models

case class AssemblyTransportTime(assembly1:Int,assembly2:Int,transportTime:Int)
case class ComponentToAssemblyTransTime(assembly:Int,component:Int,transportTime:Int)
