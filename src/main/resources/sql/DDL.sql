CREATE SCHEMA `architecture` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(16) NOT NULL,
  `NAME` varchar(250) NOT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `MOBILE` varchar(255) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `ACTIVE` bit(1) DEFAULT NULL,
  `created_by_user_id` int(11) DEFAULT NULL,
  `modified_by_user_id` int(11) DEFAULT NULL,
  `VERSION` timestamp NULL DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  UNIQUE KEY `id_UNIQUE` (`id`)
);
