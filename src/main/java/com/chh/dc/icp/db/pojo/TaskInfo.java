package com.chh.dc.icp.db.pojo;

import java.sql.Timestamp;

/**
 * 采集任务信息
 * 
 * @ClassName: TaskInfo
 * @since 1.0
 * @version 1.0
 * @author Niow
 * @date: 2016-6-27
 */
public class TaskInfo{

	/** 普通任务，只执行一次 */
	public static final int TASK_COMMON = 1;

	/** 周期任务，根据任务周期执行 */
	public static final int TASK_PERIOD = 2;

	/** 服务任务，启动后就不关闭的任务 */
	public static final int TASK_SERVICE = 3;
	
	public static final int TASK_GOLO_PERIOD = 4;

	/** 任务ID */
	private long id;

	/** 任务名称 */
	private String name;

	/** 访问器的spring bean id*/
	private String accessorId;

	/** 解析器编号spring bean id */
	private String parserId;

	/** 采集目标地址 */
	private String hostAddr;

	/** 采集目标端口 */
	private int port;

	/** 采集数据所属厂家 */
	private String vendor;

	/** 登陆采集目标使用的账户 */
	private String username;

	/** 登陆采集目标使用的密码 */
	private String password;

	/** 数据库采集使用的驱动 */
	private String dbDriver;

	/** 数据库采集使用的URL */
	private String dbURL;

	/** 采集超时设置，调整后为秒 */
	private int collectTimeoutSec;

	/** 采集周期，调整后为分钟 */
	private int collectPeriodMin;

	/** 采集目标路径 */
	private String collectPath;

	/** 任务开始执行的时间，当前时间大于此时间时，满足可以运行条件 */
	private Timestamp DataTime;

	/**
	 * 协议编码，例如FTP传输编码
	 */
	private String pathEncode;

	/** 文件编码，具体解码文件内容的编码 */
	private String fileEncode;

	/** 任务是否启用 0:停止,1:启用 */
	private int isUsed;


	/** 数据输出模板文件名称，多个文件使用;隔开 */
	private String exporterTemplate;

	/** 采集任务偏移量，目前没有明确的使用位置 */
	private int collectPos;

	/** 主机标识，主要用于telnet采集方式 */
	private String hostSign;

	/**
	 * 任务运行类型 TASK_COMMON = 1; TASK_PERIOD = 2; TASK_SERVICE = 3;
	 */
	private int taskType;

	/**
	 * 其他参数
	 */
	private String extrasArgs;
	
	/**
	 * @return the id
	 */
	public long getId(){
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id){
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName(){
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * @return the accessorId
	 */
	public String getAccessorId(){
		return accessorId;
	}

	/**
	 * @param accessorId the accessorId to set
	 */
	public void setAccessorId(String accessorId){
		this.accessorId = accessorId;
	}

	/**
	 * @return the parserId
	 */
	public String getParserId(){
		return parserId;
	}

	/**
	 * @param parserId the parserId to set
	 */
	public void setParserId(String parserId){
		this.parserId = parserId;
	}
	
	/**
	 * @return the hostAddr
	 */
	public String getHostAddr(){
		return hostAddr;
	}

	/**
	 * @param hostAddr the hostAddr to set
	 */
	public void setHostAddr(String hostAddr){
		this.hostAddr = hostAddr;
	}

	/**
	 * @return the port
	 */
	public int getPort(){
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port){
		this.port = port;
	}

	/**
	 * @return the vendor
	 */
	public String getVendor(){
		return vendor;
	}

	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor){
		this.vendor = vendor;
	}

	/**
	 * @return the username
	 */
	public String getUsername(){
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username){
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword(){
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password){
		this.password = password;
	}

	/**
	 * @return the dbDriver
	 */
	public String getDbDriver(){
		return dbDriver;
	}

	/**
	 * @param dbDriver the dbDriver to set
	 */
	public void setDbDriver(String dbDriver){
		this.dbDriver = dbDriver;
	}

	/**
	 * @return the dbURL
	 */
	public String getDbURL(){
		return dbURL;
	}

	/**
	 * @param dbURL the dbURL to set
	 */
	public void setDbURL(String dbURL){
		this.dbURL = dbURL;
	}

	/**
	 * @return the collectTimeoutSec
	 */
	public int getCollectTimeoutSec(){
		return collectTimeoutSec;
	}

	/**
	 * @param collectTimeoutSec the collectTimeoutSec to set
	 */
	public void setCollectTimeoutSec(int collectTimeoutSec){
		this.collectTimeoutSec = collectTimeoutSec;
	}

	/**
	 * @return the collectPeriodMin
	 */
	public int getCollectPeriodMin(){
		return collectPeriodMin;
	}

	/**
	 * @param collectPeriodMin the collectPeriodMin to set
	 */
	public void setCollectPeriodMin(int collectPeriodMin){
		this.collectPeriodMin = collectPeriodMin;
	}

	/**
	 * @return the collectPath
	 */
	public String getCollectPath(){
		return collectPath;
	}

	/**
	 * @param collectPath the collectPath to set
	 */
	public void setCollectPath(String collectPath){
		this.collectPath = collectPath;
	}

	/**
	 * @return the dataTime
	 */
	public Timestamp getDataTime(){
		return DataTime;
	}

	/**
	 * @param dataTime the dataTime to set
	 */
	public void setDataTime(Timestamp dataTime){
		DataTime = dataTime;
	}

	/**
	 * @return the pathEncode
	 */
	public String getPathEncode(){
		return pathEncode;
	}

	/**
	 * @param pathEncode the pathEncode to set
	 */
	public void setPathEncode(String pathEncode){
		this.pathEncode = pathEncode;
	}

	/**
	 * @return the fileEncode
	 */
	public String getFileEncode(){
		return fileEncode;
	}

	/**
	 * @param fileEncode the fileEncode to set
	 */
	public void setFileEncode(String fileEncode){
		this.fileEncode = fileEncode;
	}
	

	/**
	 * @return the isUsed
	 */
	public int getIsUsed(){
		return isUsed;
	}

	/**
	 * @param isUsed the isUsed to set
	 */
	public void setIsUsed(int isUsed){
		this.isUsed = isUsed;
	}

	public String getExporterTemplate() {
		return exporterTemplate;
	}

	public void setExporterTemplate(String exporterTemplate) {
		this.exporterTemplate = exporterTemplate;
	}

	/**
	 * @return the collectPos
	 */
	public int getCollectPos(){
		return collectPos;
	}

	/**
	 * @param collectPos the collectPos to set
	 */
	public void setCollectPos(int collectPos){
		this.collectPos = collectPos;
	}

	/**
	 * @return the hostSign
	 */
	public String getHostSign(){
		return hostSign;
	}

	/**
	 * @param hostSign the hostSign to set
	 */
	public void setHostSign(String hostSign){
		this.hostSign = hostSign;
	}

	/**
	 * @return the taskType
	 */
	public int getTaskType(){
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(int taskType){
		this.taskType = taskType;
	}

	public String getExtrasArgs() {
		return extrasArgs;
	}

	public void setExtrasArgs(String extrasArgs) {
		this.extrasArgs = extrasArgs;
	}
	
}
