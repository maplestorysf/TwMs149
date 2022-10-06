SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dojo_ranks
-- ----------------------------
DROP TABLE IF EXISTS `dojo_ranks`;
CREATE TABLE `dojo_ranks` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL DEFAULT '',
  `time` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of dojo_ranks
-- ----------------------------
INSERT INTO `dojo_ranks` VALUES ('1', '測試', '53');
INSERT INTO `dojo_ranks` VALUES ('2', '幻影', '120');
INSERT INTO `dojo_ranks` VALUES ('3', '凱文', '180');
INSERT INTO `dojo_ranks` VALUES ('4', '柴犬', '240');
SET FOREIGN_KEY_CHECKS=1;
