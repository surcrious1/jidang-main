-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: springboot_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci,
  `subject` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `author_id` bigint DEFAULT NULL,
  `create_date` datetime(6) DEFAULT NULL,
  `game_id` bigint DEFAULT NULL,
  `modify_date` datetime(6) DEFAULT NULL,
  `filename` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `filepath` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqrch6ehd19k1ei52aemmgkscy` (`author_id`),
  KEY `FK58kokkmvvj7hlwipn0q45gbsw` (`game_id`),
  CONSTRAINT `FK58kokkmvvj7hlwipn0q45gbsw` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
  CONSTRAINT `FKqrch6ehd19k1ei52aemmgkscy` FOREIGN KEY (`author_id`) REFERENCES `site_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,'유저test,게임 원신으로 테스트 게시물','테스트 게시물',1,'2024-11-14 00:00:00.000000',1,NULL,NULL,NULL),(2,'원신 테스트 게시물','원신',2,'2025-11-28 00:00:00.000000',1,NULL,NULL,NULL),(3,'림버스 테스트 게시물','림버스 컴퍼니',3,'2025-11-28 00:00:00.000000',2,NULL,NULL,NULL),(4,'스타레일 테스트 게시물','스타레일',1,'2025-11-28 00:00:00.000000',3,NULL,NULL,NULL),(5,'리버스 테스트 게시물','리버스1999',2,'2025-11-28 00:00:00.000000',4,NULL,NULL,NULL),(6,'블루아카이브 테스트 게시물-여러태그','블루아카이브',3,'2025-11-28 00:00:00.000000',5,NULL,NULL,NULL);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 14:27:54
