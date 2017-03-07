-- MySQL dump 10.13  Distrib 5.7.13, for linux-glibc2.5 (x86_64)
--
-- Host: localhost    Database: dikscheduling
-- ------------------------------------------------------
-- Server version	5.7.16-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Assembly`
--

DROP TABLE IF EXISTS `Assembly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Assembly` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS `Assembly_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Assembly_state` (
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Assembly_state`
--

--
-- Table structure for table `Component`
--

DROP TABLE IF EXISTS `Component`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Component`
--


--
-- Table structure for table `Operation`
--

DROP TABLE IF EXISTS `Operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Operation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Operation`
--
--
-- Table structure for table `simulation`
--

DROP TABLE IF EXISTS `simulation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `simulation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `desc` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SimulationAssemblyMap`
--

DROP TABLE IF EXISTS `SimulationAssemblyMap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SimulationAssemblyMap` (
  `simulation_id` int(11) NOT NULL,
  `assembly_id` int(11) NOT NULL,
  PRIMARY KEY (`simulation_id`),
  KEY `fk_SimulationAssemblyMap_1` (`assembly_id`),
  CONSTRAINT `fk_SimulationAssemblyMap_1` FOREIGN KEY (`assembly_id`) REFERENCES `Assembly` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SimulationAssemblyMap_2` FOREIGN KEY (`simulation_id`) REFERENCES `simulation` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SimulationAssemblyMap`
--



--
-- Table structure for table `assembly_operation_mapping`
--

DROP TABLE IF EXISTS `assembly_operation_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assembly_operation_mapping` (
  `assembly_id` int(11) NOT NULL,
  `operation_id` int(11) NOT NULL,
  `operation_time` int(11) NOT NULL,
  `state` varchar(255) DEFAULT NULL,
  `simulation_id` int(11) NOT NULL,
  KEY `fk_assembly_operation_mapping_1` (`assembly_id`),
  KEY `fk_assembly_operation_mapping_2_idx` (`operation_id`),
  KEY `fk_assembly_operation_mapping_3_idx` (`simulation_id`),
  KEY `fk_assembly_operation_mapping_4_idx` (`state`),
  CONSTRAINT `fk_assembly_operation_mapping_1` FOREIGN KEY (`assembly_id`) REFERENCES `Assembly` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assembly_operation_mapping_2` FOREIGN KEY (`operation_id`) REFERENCES `Operation` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_assembly_operation_mapping_3` FOREIGN KEY (`simulation_id`) REFERENCES `simulation` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_assembly_operation_mapping_4` FOREIGN KEY (`state`) REFERENCES `Assembly_state` (`name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assembly_operation_mapping`
--



--
-- Table structure for table `component_operation_mapping`
--

DROP TABLE IF EXISTS `component_operation_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_operation_mapping` (
  `component_id` int(11) NOT NULL,
  `operation_id` int(11) NOT NULL,
  `sequence` int(11) NOT NULL,
  KEY `fk_component_operation_mapping_1` (`component_id`),
  KEY `fk_component_operation_mapping_2` (`operation_id`),
  CONSTRAINT `fk_component_operation_mapping_1` FOREIGN KEY (`component_id`) REFERENCES `Component` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_component_operation_mapping_2` FOREIGN KEY (`operation_id`) REFERENCES `Operation` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_operation_mapping`


--
-- Table structure for table `component_state`
--

DROP TABLE IF EXISTS `component_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_state` (
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `name_UNIQUE1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_state`
--




--
-- Dumping data for table `simulation`


--
-- Table structure for table `simulation_component_mapping`
--

DROP TABLE IF EXISTS `simulation_component_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `simulation_component_mapping` (
  `simulation_id` int(11) NOT NULL,
  `component_id` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `assignedAssemblyId` int(11) DEFAULT NULL,
  `completedOperationIds` varchar(500) DEFAULT NULL,
  `currentOperationId` int(11) DEFAULT NULL,
  KEY `fk_simulation_component_mapping_1_idx` (`component_id`),
  KEY `fk_simulation_component_mapping_2_idx` (`simulation_id`),
  KEY `fk_simulation_component_mapping_3_idx` (`status`),
  CONSTRAINT `fk_simulation_component_mapping_1` FOREIGN KEY (`component_id`) REFERENCES `Component` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_simulation_component_mapping_2` FOREIGN KEY (`simulation_id`) REFERENCES `simulation` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_simulation_component_mapping_3` FOREIGN KEY (`status`) REFERENCES `component_state` (`name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `simulation_component_mapping`
--


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-12-29 11:16:27