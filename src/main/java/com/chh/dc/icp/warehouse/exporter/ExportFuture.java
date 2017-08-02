package com.chh.dc.icp.warehouse.exporter;


/**
 * ExportFuture 返回Exporter线程报告
 * 
 * @author chenrongqiang 2012-10-31
 */
public class ExportFuture {

	/**
	 * Exporter线程报告对象
	 */
	private ExportReport exportReport;

	public ExportReport getExportReport() {
		return exportReport;
	}

	public void setExportReport(ExportReport exportReport) {
		this.exportReport = exportReport;
	}

}
