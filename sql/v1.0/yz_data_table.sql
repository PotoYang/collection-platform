/*
Created		2016/8/18
Modified		2016/8/18
Project		
Model		
Company		
Author		
Version		
Database		mySQL 4.1 
*/








drop table IF EXISTS t_yz_realtime_data;
drop table IF EXISTS t_yz_fault_code;
drop table IF EXISTS t_yz_alarm;




Create table t_yz_alarm (
	id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
	sn Varchar(255),
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
	warning_type Int,
	warning_desc Varchar(255),
	warning_value Int,
	lpn_code Varchar(255),
	vehicle_id bigint(20),
 Primary Key (id)) ENGINE = MyISAM
ROW_FORMAT = Default;

Create table t_yz_fault_code (
	id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
	device_id bigint(20),
	dtc_value Varchar(255),
	dtc_status Int,
	gps_time Datetime,
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
 Primary Key (id)) ENGINE = MyISAM
ROW_FORMAT = Default;



Create table t_yz_realtime_data (
	id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
	device_id bigint(20),
	gps_time Datetime,
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
lamp_far int COMMENT '灯状态(远光灯)',
lamp_near int COMMENT '灯状态(近光灯)',
lamp_width int COMMENT '灯状态(示宽灯)',
lamp_fog int COMMENT '灯状态(雾灯)',
lamp_left_turn int COMMENT '灯状态(左转向)',
lamp_right_turn int COMMENT '灯状态(右转向)',
lamp_danger int COMMENT '灯状态(危险灯)',
door_left_front int COMMENT '门状态(左前门)',
door_right_front int COMMENT '门状态(右前门)',
door_left_rear int COMMENT '门状态(左后门)',
door_right_rear int COMMENT '门状态(右后门)',
door_trunk int COMMENT '门状态(后备箱\尾箱)',
lock_whole_car int COMMENT '门锁(全车锁)',
lock_left_front int COMMENT '门锁(左前门)',
lock_right_front int COMMENT '门锁(右前门)',
lock_left_rear int COMMENT '门锁(左后门)',
lock_right_rear int COMMENT '门锁(右后门)',
lock_trunk int COMMENT '门锁(后备箱\尾箱)',
window_left_front int COMMENT '窗状态(左前窗)',
window_right_front int COMMENT '窗状态(右前窗)',
window_left_rear int COMMENT '窗状态(左后窗)',
window_right_rear int COMMENT '窗状态(右后窗)',
window_dormer int COMMENT '窗状态(天窗)',
fault_signal_ecm int COMMENT '故障信号(ECM)',
fault_signal_abs int COMMENT '故障信号(ABS)',
fault_signal_srs int COMMENT '故障信号(SRS)',
fault_signal_oil int COMMENT '报警信号(机油)',
fault_signal_pressure int COMMENT '报警信号(胎压)',
fault_signal_maintenance int COMMENT '报警信号(保养)',
brake_hand int COMMENT '手刹状态',
brake_foot int COMMENT '刹车状态(脚刹)',
seat_belts_driver int COMMENT '安全带(驾驶员)',
seat_belts_copilot int COMMENT '安全带(副驾)',
acc_status int COMMENT 'ACC信号',
key_status int COMMENT '钥匙状态',
remote_control_signal int COMMENT '遥控信号',
wiper_status int COMMENT '雨刮状态',
air_conditioning_status int COMMENT '空调开关',
gear int COMMENT '档位',
all_mileage double COMMENT '里程(总)',
endurance_mileage double COMMENT '里程(续航)',
fuel_consumption double COMMENT '用油量',
water_temperature double COMMENT '水温',
engine_inlet_temperature double COMMENT '发动机进气温度',
air_conditioning_temperature double COMMENT '空调车内温度',
battery_voltage double COMMENT '电池当前电压',
wheel_speed_left_front double COMMENT '左前轮轮速',
wheel_speed_right_front double COMMENT '右前轮轮速',
wheel_speed_left_rear double COMMENT '左后轮轮速',
wheel_speed_right_rear double COMMENT '右后轮轮速',
speed double COMMENT '车速',
rotating_speed double COMMENT '转速',
fuel_consumption_average double COMMENT '油耗(平均)',
fuel_consumption_instant_km double COMMENT '油耗(瞬时)',
fuel_consumption_instant_h double COMMENT '油耗(瞬时)',
oil_lifetime double COMMENT '机油寿命',
air_flow double COMMENT '空气流量',
MAP double COMMENT '进气歧管绝对压力',
accelerator_pedal_relative_position double COMMENT '油门踏板相对位置',
accelerator int COMMENT '油门踏板',
steering_wheel_angle double COMMENT '方向盘转角角度',
steering_wheel_status int COMMENT '方向盘转向角状态',
residual_oil_volume_after_filtering_l double COMMENT '滤波后剩余油量',
residual_oil_volume_after_filtering_p double COMMENT '滤波后剩余油量',
total_mileage double COMMENT '累计里程',
trip_uid varchar(10) COMMENT '里程ID',
apk_battery_voltage double COMMENT 'APK电池当前电压',
acceleration double COMMENT '汽车加速度',
brake_pedal_relative_position double COMMENT '刹车踏板相对位置',
 Primary Key (id)) ENGINE = MyISAM
ROW_FORMAT = Default;




CREATE TABLE `t_yz_gps` (
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
   `device_id` bigint(20) unsigned NOT NULL COMMENT '设备ID',
   `gps_time` datetime NOT NULL COMMENT 'GPS时间戳',
   `latitude` double NOT NULL COMMENT '纬度',
   `longitude` double NOT NULL COMMENT '经度',
   `height` double NOT NULL COMMENT '海拔',
   `speed` double DEFAULT NULL COMMENT '瞬时速度',
   `direction` double DEFAULT NULL COMMENT '方向',
   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
   `accuracy` double DEFAULT NULL COMMENT 'GPS精度',
   `gps_locate_model` int(11) DEFAULT NULL COMMENT 'GPS坐标类型,    1 地球坐标 2 百度坐标',
   PRIMARY KEY (`id`)
 ) ENGINE=MyISAM ROW_FORMAT = Default;










/* Users permissions */




