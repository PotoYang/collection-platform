package com.chh.dc.icp.warehouse.repository;


import com.chh.dc.icp.warehouse.ParsedRecord;
import com.chh.dc.icp.warehouse.WarehouseReport;

import java.util.Collection;

public interface Repository {

	/**
	 * 获取仓库ID
	 * 
	 * @return
	 */
	long getReposId();

	/**
	 * 往仓库中写入数据
	 *
	 * @return
	 */
	int transport(Collection<ParsedRecord> parsedRecords);

	/**
	 * 往仓库中写入数据
	 *
	 * @return
	 */
	int transport(ParsedRecord parsedRecord);

	/**
	 * 提交数据
	 * 
	 */
	void commit();

	/**
	 * 数据回滚
	 * 
	 */
	void rollBack();

	/**
	 * 获取数据仓库报表
	 * 
	 * @return 数据仓库报表
	 */
	WarehouseReport getReport();
}
