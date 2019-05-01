-- MySQL dump 10.13  Distrib 5.7.25, for Linux (x86_64)
--
-- Host: localhost    Database: db_whukg
-- ------------------------------------------------------
-- Server version	5.7.25-0ubuntu0.18.04.2

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
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `depth` int(10) NOT NULL,
  `host` varchar(25) DEFAULT NULL,
  `title` varchar(150) DEFAULT NULL,
  `url` varchar(200) DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4,
  `html` longtext,
  `type` int(1) DEFAULT NULL,
  `url_len` int(11) DEFAULT NULL,
  `anchor_len` int(11) DEFAULT NULL,
  `url_type` varchar(45) DEFAULT NULL,
  `anchor` varchar(150) DEFAULT NULL,
  `parameters` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5004 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `news_common`
--

DROP TABLE IF EXISTS `news_common`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news_common` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `depth` int(10) NOT NULL,
  `host` varchar(25) CHARACTER SET utf8 DEFAULT NULL,
  `title` varchar(150) CHARACTER SET utf8 DEFAULT NULL,
  `url` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4,
  `html` longtext CHARACTER SET utf8,
  `type` int(1) DEFAULT NULL,
  `url_len` int(11) DEFAULT NULL,
  `anchor_len` int(11) DEFAULT NULL,
  `url_type` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `anchor` varchar(150) CHARACTER SET utf8 DEFAULT NULL,
  `parameters` varchar(60) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6785 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `news_topic`
--

DROP TABLE IF EXISTS `news_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news_topic` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `depth` int(10) NOT NULL,
  `topic` varchar(2) CHARACTER SET utf8mb4 NOT NULL,
  `host` varchar(25) CHARACTER SET utf8 DEFAULT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 DEFAULT NULL,
  `url` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4,
  `html` longtext CHARACTER SET utf8mb4,
  `type` int(1) DEFAULT NULL,
  `url_len` int(11) DEFAULT NULL,
  `anchor_len` int(11) DEFAULT NULL,
  `url_type` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `anchor` varchar(150) CHARACTER SET utf8mb4 DEFAULT NULL,
  `parameters` varchar(60) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=376 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-01 14:34:38
