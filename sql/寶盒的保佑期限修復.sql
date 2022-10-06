ALTER TABLE `coreauras` CHANGE `expire` `expire` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `coreauras` ADD COLUMN `delay` tinyint(1) DEFAULT '0';