package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.warehouse.DataBlock;
import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Niow on 2016/8/3.
 */
public abstract class AbstractBufferedExporter extends AbstractExporter {

    private static final Logger log = LoggerFactory.getLogger(AbstractBufferedExporter.class); // 日志

    protected LinkedList<DataBlock> dataBlockBuffer = new LinkedList<DataBlock>();

    //异步输出线程 锁
    private ReentrantLock exportLock = new ReentrantLock();

    private Condition bufferEmpty = exportLock.newCondition();


    public AbstractBufferedExporter(ExportTemplate template, TaskInfo taskInfo) {
        super(template, taskInfo);
    }


    /**
     * 缓存异步线程输出器，在此处把需要输出的数据块放入缓存队列中
     *
     * @param dataBlock
     * @throws Exception
     */
    @Override
    public void export(DataBlock dataBlock) throws Exception {
        try {
            exportLock.lockInterruptibly();
            this.dataBlockBuffer.add(dataBlock);
            bufferEmpty.signalAll();
        } catch (InterruptedException e) {
            log.error("输出线程获取Lock异常", e);
        } finally {
            exportLock.unlock();
        }
    }

    /**
     * 线程方法<br>
     * 从cache中抽取
     */
    public ExportFuture call() throws Exception {
        Thread.currentThread().setName(this.getClass().getSimpleName() + ":" + this.getDataType());
        startTime = new Timestamp(new Date().getTime());
        int exportedNum = 0;
        ExportFuture exportFuture = new ExportFuture();
        ExportReport exportReport = null;
        if (exporterInitErrorFlag) {
            this.endTime = new Timestamp(new Date().getTime());
            exportReport = createExportReport();
            log.debug("Exporter初始化失败,输出中止,报表={}", exportReport);
            exportFuture.setExportReport(exportReport);
            return exportFuture;
        }
        // 如果初始化没有失败 则进行输出
        //终止处理==true&&缓存区没有数据才能退出
        while (!(breakProcessFlag && dataBlockBuffer.size() == 0)) {
            try {
                exportLock.lockInterruptibly();
                while (dataBlockBuffer.size() == 0) {
                    bufferEmpty.awaitNanos(100000000L);
                }
                DataBlock dataBlock = dataBlockBuffer.remove(0);
                exportedNum++;
                this.total += dataBlock.getData().size();
                this.doRealExport(dataBlock);
            } catch (Exception e) {
                log.debug("输出异常", e);
                this.setErrorCode(-1);
                this.setCause(e.getMessage());
//                this.close();
//                break;
            } finally {
                exportLock.unlock();
            }
        }
        this.close();
        if (breakProcessFlag) {
            log.error("ExportFuture::call() 收到终止处理标识，export线程退出。 终止原因:{}", breakProcessCause);
        }
        endTime = new Timestamp(new Date().getTime());
        exportReport = createExportReport();
        exportFuture.setExportReport(exportReport);
        log.debug(Thread.currentThread().getName() + "，输出完毕，产生报表={}", exportReport.toString());
        return exportFuture;
    }

    /**
     * 缓存异步线程输出器，实际处理输出的方法
     *
     * @param dataBlock
     * @throws Exception
     */
    protected abstract void doRealExport(DataBlock dataBlock) throws Exception;

    @Override
    public void commit() {
        try {
            exportLock.lockInterruptibly();
            bufferEmpty.signalAll();
        } catch (InterruptedException e) {
            log.error("缓存输出器异常中断", e);
        } finally {
            exportLock.unlock();
        }
    }

    @Override
    public void breakProcess(String breakCause) {
        super.breakProcess(breakCause);
        exportLock.lock();
        try {
            bufferEmpty.signalAll();
        } finally {
            exportLock.unlock();
        }
    }

}
