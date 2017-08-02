package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.warehouse.DataBlock;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * The exporter interface defined
 * 
 * @ClassName: Exporter
 * @since 1.0
 * @version 1.0
 * @author Niow
 * @date: 2016-6-27
 */
public interface Exporter extends Callable<ExportFuture>{

	/**
	 * JDBC方式直接输出
	 */
	public static final String TYPE_JDBC_BATCH = "jdbc_batch";

	/**
	 * 使用数据库连接池并且具有容错能力的JDBC输出器
	 */
	public static final String TYPE_JDBC_POOL = "jdbc_pool";

	/**
	 * 使用数据库连接池并且具有容错能力的JDBC输出器
	 */
	public static final String TYPE_JDBC_POOL_FAULT_TOLERANT = "jdbc_pool_tolerant";


	/**
	 * 使用redis缓存作为输出目的地
	 */
	public static final String TYPE_REDIS = "redis";

	/**
	 * 输出数据块
	 *
	 * @param dataBlock
	 */
	void export(DataBlock dataBlock) throws Exception;

	/**
	 * 关闭输出器 释放资源 如DB关闭connection/statement/ResultSet ，文件输出器关闭文件流等 close方法正常情况不由外部调用 Exporter在判断数据输出结束后自己关闭释放资源。
	 */
	void close();

	/**
	 * 完成数据的提交动作，如果exporter的缓存中还存留数据，则需要把数据输出完成。
	 */
	void commit();

	/**
	 * 接口方法 Exporter实现当输出发生异常时，并且需要关闭输出器
	 */
	void endExportOnException();

	/**
	 * Get name of this Exporter
	 *
	 * @return exporter's name
	 */
	public String getName();

	/**
	 * 初始化输出器
	 */
	public void init();

	/**
	 * 获取输出器的输出模版ID
	 *
	 * @return
	 */
	public int getExportId();

	public String getDataType();

	/**
	 * 构建输出字段属性集合表
	 * @param propertysSet 用于存放当前export使用的属性集合
	 */
	void buildExportPropertysList(Set<String> propertysSet);

	/**
	 * 终止处理
	 * @param breakCause 终止原因
	 */
	void breakProcess(String breakCause);

	void setDataType(String dataType);
}
