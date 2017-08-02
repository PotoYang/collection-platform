package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.warehouse.GenericWareHouse;
import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;
import com.chh.dc.icp.warehouse.exporter.template.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 输出器管理<br>
 * 每个Repository都有一个自己的ExporterManager<br/>
 *
 *
 * @ClassName: ExporterManager
 * @since 1.0
 * @version 1.0
 * @author Niow
 * @date: 2016-6-27
 */
public class ExporterManager extends Thread{

    private static final Logger log = LoggerFactory.getLogger(ExporterManager.class);

    private TaskInfo taskInfo;

    List<ExportTemplate> exportTemplets;

    private ExecutorService es;

    private CompletionService<ExportFuture> cs;

    private List<Exporter> exporters = new ArrayList<Exporter>();

    Map<String, Integer> dataThreadCounter = new HashMap<String, Integer>();

    // 外部是否发生异常
    private boolean exceptionFlag = false;


    public ExporterManager(TaskInfo taskInfo) throws Exception {
        this.taskInfo = taskInfo;
        createExporters();
    }

    public synchronized void notifyException() {
        this.exceptionFlag = true;
    }

    /**
     * 根据输出定义和模版 动态创建exporter
     */
    void createExporters() throws Exception {
        String templateName = taskInfo.getExporterTemplate();
        List<String> templateFiles = new ArrayList<String>();
        if(templateName.contains(";")){
            String[] split = templateName.split(";");
            templateFiles.addAll(Arrays.asList(split));
        }else{
            templateFiles.add(templateName);
        }
        try {
            exportTemplets = TemplateLoader.loadTemplate(templateFiles);
        } catch (Exception e) {
            log.error("加载输出模板出错",e);
            return;
        }
        for (ExportTemplate template : exportTemplets) {
            String dataType = template.getDataType();
            Exporter exporter = ExporterFactory.createExporter(taskInfo,template);
            if (exporter != null) {
                exporter.setDataType(dataType);
                // List 中增加Exporter的一个引用，方便进行迭代和输出,以及线程池调用Exporter
                exporters.add(exporter);
                Integer dataExporterNum = dataThreadCounter.get(dataType);
                if (dataExporterNum == null) {
                    dataThreadCounter.put(dataType, 1);
                    continue;
                }
                dataThreadCounter.put(dataType, dataExporterNum + 1);
            }
            if (exporter.getDataType() == null) {
                System.out.println(exporter.getExportId()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+template.getType());
            }
        }
    }

    @Override
    public void run() {
        this.setName("ExporterManager");
        int exporterNum = exporters.size();
        if (exporterNum == 0) {
            throw new IllegalArgumentException("ExporterManager线程池无法创建，输出失败，请检查输出配置");
        }
        es = Executors.newFixedThreadPool(exporterNum);
        cs = new ExecutorCompletionService<ExportFuture>(es);
        log.debug("ExporterManager线程池创建。");

        for (int i = 0; i < exporterNum; i++) {
            cs.submit(exporters.get(i));
        }

        List<ExportReport> exportReports = new ArrayList<ExportReport>();
        for (int i = 0; i < exporterNum; i++) {
            try {
                Future<ExportFuture> exportFuture = cs.take();
                if (exportFuture == null) {
                    continue;
                }
                ExportFuture future = exportFuture.get();
                ExportReport exportReport = future.getExportReport();
                exportReports.add(exportReport);
            } catch (InterruptedException e) {
                log.error("输出器线程中断!", e);
            } catch (ExecutionException e) {
                log.error("线程返回结果处理异常!", e);
            }
        }
        afterExport(exportReports);

        // 输出完毕后关闭线程池
        shutdown();
    }



    /**
     * 输出完成后处理 更新采集记录表输出状态为1[表示输出成功]
     *
     * @param exportReports
     */
    private void afterExport(List<ExportReport> exportReports) {
        // 通知仓库 当前任务使用输出线程使用warehouse完毕
        long taskInfoId = taskInfo.getId();
        GenericWareHouse.getInstance().shutdownNotice(taskInfoId);
        String errorMsg = null;
        for (ExportReport exportReport : exportReports) {
            log.debug("exportReport=" + exportReport);
            if (exportReport.getCause() != null && errorMsg == null) {
                errorMsg = exportReport.getCause();
            }
        }
        // 修改状态记录表 标志输出完成 如果所有Exporter 失败原因均为空，则表示全部成功
//        if (errorMsg == null) {
//            List<Status> status = exporterArgs.getObjStatus();
//            int statusNum = status.size();
//            for (int i = 0; i < statusNum; i++) {
//                //statusDAO.updateExportStatus(status.get(i).getId(), exceptionFlag ? 0 : 1);
//                status.get(i).updateExportStatusBySynchronized(statusDAO, status.get(i).getId(), exceptionFlag ? 0 : 1);
//            }
//        }
    }

    public List<Exporter> getExporters() {
        return exporters;
    }

    void shutdown() {
        if (es != null) {
            es.shutdown();
            log.debug("ExporterManager线程池关闭。");
        }
    }

}