-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.35 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for smtp
CREATE DATABASE IF NOT EXISTS `smtp` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smtp`;

-- Dumping structure for table smtp.admin
CREATE TABLE IF NOT EXISTS `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.admin: ~0 rows (approximately)
REPLACE INTO `admin` (`id`, `username`, `password`) VALUES
	(1, '200312713429', '123456');

-- Dumping structure for table smtp.attendance
CREATE TABLE IF NOT EXISTS `attendance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `class_id` int NOT NULL,
  `student_sno` int NOT NULL,
  `status` int NOT NULL COMMENT '1= present, 2= absent',
  PRIMARY KEY (`id`),
  KEY `fk_attendance_class1_idx` (`class_id`),
  KEY `fk_attendance_student1_idx` (`student_sno`),
  CONSTRAINT `fk_attendance_class1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  CONSTRAINT `fk_attendance_student1` FOREIGN KEY (`student_sno`) REFERENCES `student` (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.attendance: ~2 rows (approximately)
REPLACE INTO `attendance` (`id`, `class_id`, `student_sno`, `status`) VALUES
	(18, 1, 1, 1),
	(19, 1, 2, 2),
	(20, 2, 1, 1);

-- Dumping structure for table smtp.class
CREATE TABLE IF NOT EXISTS `class` (
  `id` int NOT NULL AUTO_INCREMENT,
  `teacher_tno` int NOT NULL,
  `subject_subno` int NOT NULL,
  `date` date DEFAULT NULL,
  `startAt` varchar(25) DEFAULT NULL,
  `endAt` varchar(25) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_class_teacher1_idx` (`teacher_tno`),
  KEY `fk_class_subject1_idx` (`subject_subno`),
  CONSTRAINT `fk_class_subject1` FOREIGN KEY (`subject_subno`) REFERENCES `subject` (`subno`),
  CONSTRAINT `fk_class_teacher1` FOREIGN KEY (`teacher_tno`) REFERENCES `teacher` (`tno`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.class: ~4 rows (approximately)
REPLACE INTO `class` (`id`, `teacher_tno`, `subject_subno`, `date`, `startAt`, `endAt`, `status`) VALUES
	(1, 1, 1, '2024-05-13', '02:44 PM', '08:44 PM', '1'),
	(2, 1, 1, '2024-05-13', '02:39 PM', '08:39 PM', '2'),
	(3, 4, 5, '2024-05-13', '11:02 AM', '12:02 PM', '1'),
	(4, 3, 3, '2024-05-13', '11:02 AM', '12:02 PM', '1'),
	(5, 1, 1, '2024-05-13', '07:19 PM', '12:00 AM', '1'),
	(6, 3, 3, '2024-05-13', '09:17 AM', '10:30 AM', '1');

-- Dumping structure for table smtp.gender
CREATE TABLE IF NOT EXISTS `gender` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.gender: ~2 rows (approximately)
REPLACE INTO `gender` (`id`, `type`) VALUES
	(1, 'Male'),
	(2, 'Female');

-- Dumping structure for table smtp.invoice
CREATE TABLE IF NOT EXISTS `invoice` (
  `id` varchar(20) NOT NULL,
  `student_sno` int NOT NULL,
  `date` date NOT NULL,
  `discount` double NOT NULL,
  `total` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_invoice_item_student1_idx` (`student_sno`),
  CONSTRAINT `fk_invoice_item_student1` FOREIGN KEY (`student_sno`) REFERENCES `student` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.invoice: ~0 rows (approximately)
REPLACE INTO `invoice` (`id`, `student_sno`, `date`, `discount`, `total`) VALUES
	('#ace8eca0', 1, '2024-05-12', 0, 25000);

-- Dumping structure for table smtp.invoice_item
CREATE TABLE IF NOT EXISTS `invoice_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `teacher_tno` int NOT NULL,
  `invoice_id` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_invoice_item_teacher1_idx` (`teacher_tno`),
  KEY `fk_invoice_item_invoice1_idx` (`invoice_id`),
  CONSTRAINT `fk_invoice_item_invoice1` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `fk_invoice_item_teacher1` FOREIGN KEY (`teacher_tno`) REFERENCES `teacher` (`tno`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.invoice_item: ~0 rows (approximately)
REPLACE INTO `invoice_item` (`id`, `teacher_tno`, `invoice_id`) VALUES
	(14, 1, '#ace8eca0');

-- Dumping structure for table smtp.student
CREATE TABLE IF NOT EXISTS `student` (
  `sno` int NOT NULL AUTO_INCREMENT,
  `nic` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `gender_id` int NOT NULL,
  PRIMARY KEY (`sno`),
  KEY `fk_student_gender_idx` (`gender_id`),
  CONSTRAINT `fk_student_gender` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.student: ~3 rows (approximately)
REPLACE INTO `student` (`sno`, `nic`, `mobile`, `firstName`, `lastName`, `dob`, `address`, `gender_id`) VALUES
	(1, '200834162719', '0761634288', 'Sanuth', 'Nenuka', '2008-05-07', '12/5, Matara, Sri Lanka', 1),
	(2, '200316342876', '0762849319', 'Pasan', 'Mihisara', '2003-07-30', '12/4, Hakmana, Sri Lanka', 1),
	(3, '200346284976', '0762846179', 'Pasindu', 'Muthumina', '2003-08-09', '12/8, Matara, Sri Lanka', 1),
	(5, '200349821679', '0769824167', 'Sangeetha', 'Wijeman', '2005-05-24', '12/5, Matara, Sri Lanka', 2);

-- Dumping structure for table smtp.subject
CREATE TABLE IF NOT EXISTS `subject` (
  `subno` int NOT NULL AUTO_INCREMENT,
  `description` text,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`subno`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.subject: ~4 rows (approximately)
REPLACE INTO `subject` (`subno`, `description`, `price`) VALUES
	(1, 'Sinhala', 25000),
	(2, 'Maths', 50000),
	(3, 'Science', 75000),
	(4, 'English', 45000),
	(5, 'History', 100000),
	(6, 'Civil', 50000);

-- Dumping structure for table smtp.teacher
CREATE TABLE IF NOT EXISTS `teacher` (
  `tno` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `mobile` varchar(10) DEFAULT NULL,
  `nic` varchar(15) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `subject_subno` int NOT NULL,
  `gender_id` int NOT NULL,
  PRIMARY KEY (`tno`),
  KEY `fk_teacher_subject1_idx` (`subject_subno`),
  KEY `fk_teacher_gender1_idx` (`gender_id`),
  CONSTRAINT `fk_teacher_gender1` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`),
  CONSTRAINT `fk_teacher_subject1` FOREIGN KEY (`subject_subno`) REFERENCES `subject` (`subno`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table smtp.teacher: ~4 rows (approximately)
REPLACE INTO `teacher` (`tno`, `firstName`, `lastName`, `mobile`, `nic`, `address`, `subject_subno`, `gender_id`) VALUES
	(1, 'kusal', 'Mendis', '0762819349', '199934162876', '12/A, Colombo, Sri Lanka', 1, 1),
	(3, 'Pansilu', 'Perera', '0762849316', '199924381627', '12/4, Colombo, Sri Lanka', 3, 1),
	(4, 'Kumari', 'Mudalige', '0741685234', '199428462317', '12/9, Colombo, Sri Lanka', 5, 2),
	(5, 'Pramitha', 'Pahan', '0769768249', '199958316749', '12/6, Katharagama, Sri Lanka', 6, 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
