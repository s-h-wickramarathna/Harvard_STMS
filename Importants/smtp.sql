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

-- Dumping data for table smtp.admin: ~1 rows (approximately)
REPLACE INTO `admin` (`id`, `username`, `password`) VALUES
	(1, '200312713429', '123456');

-- Dumping data for table smtp.attendance: ~3 rows (approximately)
REPLACE INTO `attendance` (`id`, `class_id`, `student_sno`, `status`) VALUES
	(18, 1, 1, 1),
	(19, 1, 2, 2),
	(20, 2, 1, 1);

-- Dumping data for table smtp.class: ~5 rows (approximately)
REPLACE INTO `class` (`id`, `teacher_tno`, `subject_subno`, `date`, `startAt`, `endAt`, `status`) VALUES
	(1, 1, 1, '2024-05-13', '02:44 PM', '08:44 PM', '1'),
	(2, 1, 1, '2024-05-13', '02:39 PM', '08:39 PM', '2'),
	(3, 4, 5, '2024-05-13', '11:02 AM', '12:02 PM', '1'),
	(4, 3, 3, '2024-05-13', '11:02 AM', '12:02 PM', '1'),
	(5, 1, 1, '2024-05-13', '07:19 PM', '12:00 AM', '1'),
	(6, 3, 3, '2024-05-13', '09:17 AM', '10:30 AM', '1');

-- Dumping data for table smtp.gender: ~2 rows (approximately)
REPLACE INTO `gender` (`id`, `type`) VALUES
	(1, 'Male'),
	(2, 'Female');

-- Dumping data for table smtp.invoice: ~1 rows (approximately)
REPLACE INTO `invoice` (`id`, `student_sno`, `date`, `discount`, `total`) VALUES
	('#ace8eca0', 1, '2024-05-12', 0, 25000);

-- Dumping data for table smtp.invoice_item: ~1 rows (approximately)
REPLACE INTO `invoice_item` (`id`, `teacher_tno`, `invoice_id`) VALUES
	(14, 1, '#ace8eca0');

-- Dumping data for table smtp.student: ~4 rows (approximately)
REPLACE INTO `student` (`sno`, `nic`, `mobile`, `firstName`, `lastName`, `dob`, `address`, `gender_id`) VALUES
	(1, '200834162719', '0761634288', 'Sanuth', 'Nenuka', '2008-05-07', '12/5, Matara, Sri Lanka', 1),
	(2, '200316342876', '0762849319', 'Pasan', 'Mihisara', '2003-07-30', '12/4, Hakmana, Sri Lanka', 1),
	(3, '200346284976', '0762846179', 'Pasindu', 'Muthumina', '2003-08-09', '12/8, Matara, Sri Lanka', 1),
	(5, '200349821679', '0769824167', 'Sangeetha', 'Wijeman', '2005-05-24', '12/5, Matara, Sri Lanka', 2);

-- Dumping data for table smtp.subject: ~6 rows (approximately)
REPLACE INTO `subject` (`subno`, `description`, `price`) VALUES
	(1, 'Sinhala', 25000),
	(2, 'Maths', 50000),
	(3, 'Science', 75000),
	(4, 'English', 45000),
	(5, 'History', 100000),
	(6, 'Civil', 50000);

-- Dumping data for table smtp.teacher: ~3 rows (approximately)
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
