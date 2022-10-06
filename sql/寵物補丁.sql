ALTER TABLE `pets` ADD COLUMN `skillid` int(11) DEFAULT 0 AFTER `flags`;
ALTER TABLE `pets` ADD COLUMN `canPickUp` smallint(3) DEFAULT 0 AFTER `skillid`;