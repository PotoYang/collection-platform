SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_icp_task
-- V1.0 init
-- ----------------------------
DROP TABLE IF EXISTS `t_icp_task`;

CREATE TABLE `t_icp_task` (
  `id` int(11) DEFAULT NULL COMMENT '任务ID，唯一标识',
  `name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `accessor_id` varchar(255) DEFAULT NULL COMMENT '采集访问器的spring bean id',
  `parser_id` varchar(255) DEFAULT NULL COMMENT '采集解析器的spring bean id',
  `exporter_template` varchar(255) DEFAULT NULL COMMENT '采集数据输出模板的文件名称,多个使用英文分好;进行分割',
  `host_addr` varchar(255) DEFAULT NULL COMMENT '采集目标机器的IP或者域名',
  `host_port` int(11) DEFAULT NULL COMMENT '采集目标机器的端口',
  `username` varchar(255) DEFAULT NULL COMMENT '如果采集目标需要登录，此处填写登录账号',
  `password` varchar(255) DEFAULT NULL COMMENT '如果采集目标需要登录，此处填写登录密码',
  `db_driver` varchar(255) DEFAULT NULL COMMENT '从数据库采集数据时，填写数据库驱动类型',
  `db_url` varchar(255) DEFAULT NULL COMMENT '从数据库采集时，填写数据库JDBC的URL',
  `collection_timeout_sec` decimal(10,0) DEFAULT NULL COMMENT '数据采集响应超时秒数',
  `collection_period_sec` int(11) DEFAULT NULL COMMENT '任务采集周期秒数，任务类型为2周期任务时生效',
  `excution_time` datetime DEFAULT NULL COMMENT '任务执行时间，当任务为普通任务时，采集时间到达时，任务启动；如果为周期任务，在任务本次执行结束时，会更新任务下次执行的时间',
  `collection_path` text COMMENT '当采集任务属于文件路径采集时，填写FTP、HTTP等路径',
  `path_encode` varchar(255) DEFAULT NULL COMMENT '采集路径的编码格式，通常匹配FTP服务器使用，如果HTTP路径是编码过的，也需要在此填写',
  `file_encode` varchar(255) DEFAULT NULL COMMENT '如果采集的数据是文件类型，如果不是默认UTF-8格式，需要填写编码集',
  `host_sign` varchar(255) DEFAULT NULL COMMENT 'telnet方式采集数据时，有时候需要填写本机标识',
  `task_type` int(11) DEFAULT NULL COMMENT '任务类型：1：普通任务，执行一次；2：周期任务，循环执行；3：服务任务，开启后就不关闭，由数据源主动推送数据',
  `is_used` int(11) DEFAULT NULL COMMENT '任务是否有效：1：有效；0：无效',
  `extras_args` varchar(255) DEFAULT NULL COMMENT '其他参数'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


CREATE TABLE `t_yz_device_uid` (
  `id` varchar(50) NOT NULL,
	`device_uid` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `T_ICP_TASK` */

insert  into `t_icp_task`(`id`,`name`,`accessor_id`,`parser_id`,`exporter_template`,`host_addr`,`host_port`,`username`,`password`,`db_driver`,`db_url`,`collection_timeout_sec`,`collection_period_sec`,`excution_time`,`collection_path`,`path_encode`,`file_encode`,`host_sign`,`task_type`,`is_used`,`extras_args`) values (1,'Redis_DNA','redisStackAccessor','dnaGid22Parser','dna-export-template.xml','localhost',6379,'','','','','1000',3,'2016-07-13 11:57:41','dna_obd_data','','','',3,0,NULL),(2,'Http_GOLO_GPS','goloHttpAccessor','goloOpenApiParser','yz-export-template.xml',NULL,NULL,NULL,NULL,'com.mysql.jdbc.Driver',NULL,NULL,10,'2016-08-13 19:07:41',NULL,NULL,NULL,NULL,4,0,'{\"type\":2,\"begin\":1,\"end\":100000}'),(3,'Http_GOLO_realtime_data','goloHttpAccessor','goloOpenApiParser','yz-export-template.xml',NULL,NULL,NULL,NULL,'com.mysql.jdbc.Driver',NULL,NULL,10,'2016-08-13 19:37:41',NULL,NULL,NULL,NULL,4,1,'{\"type\":1,\"begin\":1,\"end\":100000}'),(4,'Http_GOLO_fault_code','goloHttpAccessor','goloOpenApiParser','yz-export-template.xml',NULL,NULL,NULL,NULL,'com.mysql.jdbc.Driver',NULL,NULL,10,'2016-08-13 22:37:41',NULL,NULL,NULL,NULL,4,0,'{\"type\":3,\"begin\":1,\"end\":100000}');
