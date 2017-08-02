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
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '���ʱ��',
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
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '���ʱ��',
 Primary Key (id)) ENGINE = MyISAM
ROW_FORMAT = Default;



Create table t_yz_realtime_data (
	id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
	device_id bigint(20),
	gps_time Datetime,
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '���ʱ��',
lamp_far int COMMENT '��״̬(Զ���)',
lamp_near int COMMENT '��״̬(�����)',
lamp_width int COMMENT '��״̬(ʾ���)',
lamp_fog int COMMENT '��״̬(���)',
lamp_left_turn int COMMENT '��״̬(��ת��)',
lamp_right_turn int COMMENT '��״̬(��ת��)',
lamp_danger int COMMENT '��״̬(Σ�յ�)',
door_left_front int COMMENT '��״̬(��ǰ��)',
door_right_front int COMMENT '��״̬(��ǰ��)',
door_left_rear int COMMENT '��״̬(�����)',
door_right_rear int COMMENT '��״̬(�Һ���)',
door_trunk int COMMENT '��״̬(����\β��)',
lock_whole_car int COMMENT '����(ȫ����)',
lock_left_front int COMMENT '����(��ǰ��)',
lock_right_front int COMMENT '����(��ǰ��)',
lock_left_rear int COMMENT '����(�����)',
lock_right_rear int COMMENT '����(�Һ���)',
lock_trunk int COMMENT '����(����\β��)',
window_left_front int COMMENT '��״̬(��ǰ��)',
window_right_front int COMMENT '��״̬(��ǰ��)',
window_left_rear int COMMENT '��״̬(���)',
window_right_rear int COMMENT '��״̬(�Һ�)',
window_dormer int COMMENT '��״̬(�촰)',
fault_signal_ecm int COMMENT '�����ź�(ECM)',
fault_signal_abs int COMMENT '�����ź�(ABS)',
fault_signal_srs int COMMENT '�����ź�(SRS)',
fault_signal_oil int COMMENT '�����ź�(����)',
fault_signal_pressure int COMMENT '�����ź�(̥ѹ)',
fault_signal_maintenance int COMMENT '�����ź�(����)',
brake_hand int COMMENT '��ɲ״̬',
brake_foot int COMMENT 'ɲ��״̬(��ɲ)',
seat_belts_driver int COMMENT '��ȫ��(��ʻԱ)',
seat_belts_copilot int COMMENT '��ȫ��(����)',
acc_status int COMMENT 'ACC�ź�',
key_status int COMMENT 'Կ��״̬',
remote_control_signal int COMMENT 'ң���ź�',
wiper_status int COMMENT '���״̬',
air_conditioning_status int COMMENT '�յ�����',
gear int COMMENT '��λ',
all_mileage double COMMENT '���(��)',
endurance_mileage double COMMENT '���(����)',
fuel_consumption double COMMENT '������',
water_temperature double COMMENT 'ˮ��',
engine_inlet_temperature double COMMENT '�����������¶�',
air_conditioning_temperature double COMMENT '�յ������¶�',
battery_voltage double COMMENT '��ص�ǰ��ѹ',
wheel_speed_left_front double COMMENT '��ǰ������',
wheel_speed_right_front double COMMENT '��ǰ������',
wheel_speed_left_rear double COMMENT '���������',
wheel_speed_right_rear double COMMENT '�Һ�������',
speed double COMMENT '����',
rotating_speed double COMMENT 'ת��',
fuel_consumption_average double COMMENT '�ͺ�(ƽ��)',
fuel_consumption_instant_km double COMMENT '�ͺ�(˲ʱ)',
fuel_consumption_instant_h double COMMENT '�ͺ�(˲ʱ)',
oil_lifetime double COMMENT '��������',
air_flow double COMMENT '��������',
MAP double COMMENT '������ܾ���ѹ��',
accelerator_pedal_relative_position double COMMENT '����̤�����λ��',
accelerator int COMMENT '����̤��',
steering_wheel_angle double COMMENT '������ת�ǽǶ�',
steering_wheel_status int COMMENT '������ת���״̬',
residual_oil_volume_after_filtering_l double COMMENT '�˲���ʣ������',
residual_oil_volume_after_filtering_p double COMMENT '�˲���ʣ������',
total_mileage double COMMENT '�ۼ����',
trip_uid varchar(10) COMMENT '���ID',
apk_battery_voltage double COMMENT 'APK��ص�ǰ��ѹ',
acceleration double COMMENT '�������ٶ�',
brake_pedal_relative_position double COMMENT 'ɲ��̤�����λ��',
 Primary Key (id)) ENGINE = MyISAM
ROW_FORMAT = Default;




CREATE TABLE `t_yz_gps` (
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
   `device_id` bigint(20) unsigned NOT NULL COMMENT '�豸ID',
   `gps_time` datetime NOT NULL COMMENT 'GPSʱ���',
   `latitude` double NOT NULL COMMENT 'γ��',
   `longitude` double NOT NULL COMMENT '����',
   `height` double NOT NULL COMMENT '����',
   `speed` double DEFAULT NULL COMMENT '˲ʱ�ٶ�',
   `direction` double DEFAULT NULL COMMENT '����',
   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '���ʱ��',
   `accuracy` double DEFAULT NULL COMMENT 'GPS����',
   `gps_locate_model` int(11) DEFAULT NULL COMMENT 'GPS��������,    1 �������� 2 �ٶ�����',
   PRIMARY KEY (`id`)
 ) ENGINE=MyISAM ROW_FORMAT = Default;










/* Users permissions */




