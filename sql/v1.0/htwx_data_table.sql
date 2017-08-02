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
DROP TABLE
IF EXISTS t_htwx_gps;

CREATE TABLE t_htwx_gps (
	id BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT,
	utctime Datetime,
	speed INT,
	dir INT,
	lat DOUBLE,
	lon DOUBLE,
	satellites INT,
	device_id VARCHAR (255),
	collection_time Datetime,
	PRIMARY KEY (id)
) ENGINE = MyISAM ROW_FORMAT = DEFAULT;

DROP TABLE
IF EXISTS t_htwx_stat;

CREATE TABLE t_htwx_stat (
	id BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT,
	device_id VARCHAR (255),
	last_accon_time Datetime,
	utctime Datetime,
	total_trip_mileage BIGINT (20),
	current_trip_milea BIGINT (20),
	total_fuel DOUBLE,
	current_fuel DOUBLE,
	s07 INT,
	s06 INT,
	s05 INT,
	s04 INT,
	s03 INT,
	s02 INT,
	s01 INT,
	s00 INT,
	s17 INT,
	s16 INT,
	s15 INT,
	s14 INT,
	s13 INT,
	s12 INT,
	s11 INT,
	s10 INT,
	s27 INT,
	s26 INT,
	s25 INT,
	s24 INT,
	s23 INT,
	s22 INT,
	s21 INT,
	s20 INT,
	s37 INT,
	s36 INT,
	s35 INT,
	s34 INT,
	s33 INT,
	s32 INT,
	s31 INT,
	s30 INT,
	reserve VARCHAR (255),
	collection_time Datetime,
	PRIMARY KEY (id)
) ENGINE = MyISAM ROW_FORMAT = DEFAULT;

/* Users permissions */
DROP TABLE
IF EXISTS t_htwx_can;

CREATE TABLE t_htwx_can (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	device_id VARCHAR (255),
	utctime Datetime,
	collection_time Datetime,
	`f_2100` BIGINT,
	`f_2101` BIGINT,
	`f_2102` INT,
	`f_2103` INT,
	`f_2104` INT,
	`f_2105` INT,
	`f_2106` INT,
	`f_2107` INT,
	`f_2108` INT,
	`f_2109` INT,
	`f_210a` INT,
	`f_210b` INT,
	`f_210c` INT,
	`f_210d` INT,
	`f_210e` INT,
	`f_210f` INT,
	`f_2110` DOUBLE,
	`f_2111` INT,
	`f_2112` INT,
	`f_2113` INT,
	`f_2114` INT,
	`f_2115` INT,
	`f_2116` INT,
	`f_2117` INT,
	`f_2118` INT,
	`f_2119` INT,
	`f_211a` INT,
	`f_211b` INT,
	`f_211c` INT,
	`f_211d` INT,
	`f_211e` INT,
	`f_211f` INT,
	`f_2120` BIGINT,
	`f_2121` INT,
	`f_2122` INT,
	`f_2123` INT,
	`f_2124` INT,
	`f_2125` INT,
	`f_2126` INT,
	`f_2127` INT,
	`f_2128` INT,
	`f_2129` INT,
	`f_212a` INT,
	`f_212b` INT,
	`f_212c` INT,
	`f_212d` INT,
	`f_212e` INT,
	`f_212f` INT,
	`f_2130` INT,
	`f_2131` INT,
	`f_2132` INT,
	`f_2133` INT,
	`f_2134` INT,
	`f_2135` INT,
	`f_2136` INT,
	`f_2137` INT,
	`f_2138` INT,
	`f_2139` INT,
	`f_213a` INT,
	`f_213b` INT,
	`f_213c` INT,
	`f_213d` INT,
	`f_213e` INT,
	`f_213f` INT,
	`f_2140` BIGINT,
	`f_2141` BIGINT,
	`f_2142` INT,
	`f_2143` INT,
	`f_2144` INT,
	`f_2145` INT,
	`f_2146` INT,
	`f_2147` INT,
	`f_2148` INT,
	`f_2149` INT,
	`f_214a` INT,
	`f_214b` INT,
	`f_214c` INT,
	`f_214d` INT,
	`f_214e` INT,
	`f_214f` BIGINT,
	`f_2150` INT,
	`f_2151` INT,
	`f_2152` INT,
	`f_2153` INT,
	`f_2154` INT,
	`f_2155` INT,
	`f_2156` INT,
	`f_2157` INT,
	`f_2158` INT,
	`f_2159` INT,
	`f_215a` INT,
	`f_215b` INT,
	`f_215c` INT,
	`f_215d` INT,
	`f_215e` INT,
	`f_2160` BIGINT,
	`f_2161` INT,
	`f_2162` INT,
	`f_2163` INT,
	`f_2164` BIGINT,
	PRIMARY KEY (id)
) ENGINE = MyISAM ROW_FORMAT = DEFAULT;

DROP TABLE
IF EXISTS t_htwx_fault;

CREATE TABLE t_htwx_fault (
	id BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT,
	device_id VARCHAR (255),
	utctime Datetime,
	collection_time Datetime,
	fault_flag INT,
	fault_count INT,
	fault_code VARCHAR (255),
	PRIMARY KEY (id)
) ENGINE = MyISAM ROW_FORMAT = DEFAULT;


DROP TABLE
IF EXISTS t_htwx_alarm;

CREATE TABLE t_htwx_alarm (
	id BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT,
	device_id VARCHAR (255),
	utctime Datetime,
	collection_time Datetime,
	new_alarm_flag INT,
	alarm_type varchar(20),
	alarm_desc INT,
	alarm_threshold INT,
	PRIMARY KEY (id)
) ENGINE = MyISAM ROW_FORMAT = DEFAULT;



