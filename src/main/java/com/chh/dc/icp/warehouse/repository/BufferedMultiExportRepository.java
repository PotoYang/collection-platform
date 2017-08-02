//package com.tcl.paas.icp.warehouse.repository;
//
//import TaskInfo;
//import ThreadUtil;
//import DataBlock;
//import ParsedRecord;
//import WarehouseReport;
//import AbstractExporter;
//import Exporter;
//import ExporterManager;
//import RandomAccessFilesExporter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * Created by Niow on 2016/7/6.
// */
//public class BufferedMultiExportRepository implements Repository {
//
//    // 日志
//    private static final Logger log = LoggerFactory.getLogger(BufferedMultiExportRepository.class);
//
//    // 仓库暂存数据块大小
//    private static final int REGITSTER_SIZE = 100;//AppContext.getBean("tempRegisterSize", Integer.class);
//
//    private long id; // 仓库ID
//
//    private Map<String, List<ParsedRecord>> tempRegisters = new HashMap<String, List<ParsedRecord>>();
//
//    private List<Exporter> exports;
//
//    private WarehouseReport warehouseReport;
//
//    private Map<String, Integer> distributeNum = new HashMap<String, Integer>();
//
//    private boolean distributedFlag;
//
//    private long total = 0L;
//
//    private long succ = 0L;
//
//    private long fail = 0L;
//
//    private String cause;
//
//    private ExporterManager exporterManager;
//
//    private DistributeThread distributeThread = null;
//
//    private TaskInfo taskInfo;
//
//    // DataBlock数据队列 用于缓存DataBlock 用于解决解码和warehouse模块的速度差异
//    private List<DataBlock> dataQueue = new LinkedList<DataBlock>();
//
//    private int maxNum = 30;
//
//    // BufferedMultiExportRepository和异步输出线程 锁
//    private ReentrantLock lock = new ReentrantLock();
//
//    // BufferedMultiExportRepository和异步输出线程 锁condition 如果dataQueue为空 则异步输出线程挂起
//    private Condition empty = lock.newCondition();
//
//    private Condition full = lock.newCondition();
//
//    // 数据提交标记
//    private volatile boolean commitFlag = false;
//
//    // 已经发生错误的exporter列表 如果已经发生错误 则从输出列表中清楚
//    private Map<Integer, Exporter> errorExporters = new HashMap<Integer, Exporter>();
//
//    private RepositoryArgs repositoryArgs;
//
//
//    public BufferedMultiExportRepository(RepositoryArgs repositoryArgs) {
//        this.repositoryArgs = repositoryArgs;
//        this.taskInfo = repositoryArgs.getTaskInfo();
//        this.warehouseReport = new WarehouseReport();
//        warehouseReport.setStartTime(new Date());
//        exporterManager = new ExporterManager(repositoryArgs.getTaskInfo());
//        this.exports = exporterManager.getExporters();
//        exporterManager.start();
//    }
//
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
//
//    /**
//     * 将写满的暂存器中数据分发至Exporter
//     */
//    private void distribute(DataBlock dataBlock) {
//        String dataType = dataBlock.getDataType();
//        int dataBlockNum = dataBlock.getData().size();
//        if (exports.size() == 0 || exports.size() == errorExporters.size()) {
//            log.warn("任务ID={},仓库ID={}没有开启输出线程或输出线程已全部失败.不在继续分发。", new Object[]{taskInfo.getId(), this.id});
//            return;
//        }
//        int exporterId = -1;
//        Exporter exporter = null;
//        Iterator<Exporter> iterator = exports.iterator();
//        while (iterator.hasNext()) {
//            try {
//                exporter = iterator.next();
//                exporterId = exporter.getExportId();
//                // 如果已经发生过错误的exporter则不用再往里对应的缓存中写入DataBlock
//                if (errorExporters.containsKey(exporterId))
//                    continue;
//                // 1)如果dataType相同，则输出；
//                // 2)如果不同，则判断是否汇总文件输出，如果是，并且包含dataType，则输出
//                if (exporter.getDataType().equalsIgnoreCase(dataType)) {
//                    exporter.export(dataBlock);
//                } else if (exporter instanceof RandomAccessFilesExporter) {
//                    RandomAccessFilesExporter randomAccessFilesExporter = (RandomAccessFilesExporter) exporter;
//                }
//            } catch (Exception e) {
//                // 如果已经发生过错误.则将当前Exporter加入到错误map中.在输入的时候不在写入
//                log.warn("任务ID={},仓库ID={},输出模版ID={}输出器发生异常已关闭", new Object[]{taskInfo.getId(), this.id, exporterId}, e);
//                errorExporters.put(exporterId, exporter);
//                exporter.breakProcess(e.getMessage());
//            }
//        }
//        // 更新已分发条数
//        Integer distributedNum = distributeNum.get(dataType);
//        if (distributedNum == null) {
//            distributedNum = 0;
//        }
//        distributedNum += dataBlockNum;
//        distributeNum.put(dataType, distributedNum);
//        this.succ += dataBlockNum;
//    }
//
//    /**
//     * 获取指定数据类型，所需要用到的字段属性
//     *
//     * @param dataType 数据类型
//     * @return
//     */
//    public Set<String> getExportUsesPropertys(String dataType) {
//        Set<String> propertysSet = new HashSet<String>();
//        if (exports.size() == 0) {
//            log.warn("任务ID={},仓库ID={}没有开启输出线程或输出线程已全部失败.查找export使用的key失败。", new Object[]{taskInfo.getId(), this.id});
//            return propertysSet;
//        }
//
//        Exporter exporter = null;
//        Iterator<Exporter> iterator = exports.iterator();
//        while (iterator.hasNext()) {
//            exporter = iterator.next();
//            if (dataType == exporter.getDataType()) {
//                exporter.buildExportPropertysList(propertysSet);
//            } else if (exporter instanceof RandomAccessFilesExporter) {
//                RandomAccessFilesExporter randomAccessFilesExporter = (RandomAccessFilesExporter) exporter;
////                if (randomAccessFilesExporter.srcDataTypes.contains(dataType)) {
////                    randomAccessFilesExporter.buildExportPropertysList(propertysSet);
////                }
//            }
//        }
//
//        return propertysSet;
//    }
//
//    @Override
//    public long getReposId() {
//        return 0;
//    }
//
//    @Override
//    public int transport(Collection<ParsedRecord> parsedRecords) {
//        return 0;
//    }
//
//    /**
//     * Repository获取一条解码数据方法
//     */
//    public int transport(ParsedRecord parsedRecord) {
//        this.total++;
//        String dataType = parsedRecord.getType();
//        List<ParsedRecord> dataList = tempRegisters.get(dataType);
//        // 如果dataList没有进行初始化 则先初始化
//        if (dataList == null) {
//            dataList = new ArrayList<ParsedRecord>(REGITSTER_SIZE);
//            dataList.add(parsedRecord);
//            tempRegisters.put(dataType, dataList);
//            return 1;
//        }
//        int size = dataList.size();
//        // 如果没有达到最大的限制 则直接放入到dataList中
//        if (size < REGITSTER_SIZE) {
//            dataList.add(parsedRecord);
//            return 1;
//        }
//        // 如果dataList已经满了 则将DataBlock添加至传输队列中 ，然后再重新创建一个缓存放入数据
//        DataBlock dataBlock = new DataBlock(tempRegisters.get(dataType), dataType);
//        addQueue(dataBlock);
//        dataList = new ArrayList<ParsedRecord>(REGITSTER_SIZE);
//        dataList.add(parsedRecord);
//        tempRegisters.put(dataType, dataList);
//        return 1;
//    }
//
//    /**
//     * 数据写入完成 如果暂存器中还有数据 继续分发至Exporter<br>
//     * 注意：设置commitFlag=true和addQueue有先后顺序，先addQueue再commit,
//     * 保证最后一条dataBlock能够被分发线程获取到
//     */
//    @Override
//    public void commit(boolean exceptionFlag) {
//        // 通知输出线程解析发生异常,则输出线程不会将export_status修改为1
//        if (exceptionFlag && exporterManager != null) {
//            // exporterManager.notifyException();
//        }
//        Set<String> types = tempRegisters.keySet();
//        for (String type : types) {
//            List<ParsedRecord> dataList = tempRegisters.get(type);
//            int size = dataList.size();
//            if (size == 0) {
//                continue;
//            }
//            if (exceptionFlag) {
//                continue;
//            }
//            // 如果有数据 并且未发生异常 则将剩下的数据放入缓存用于输出
//            DataBlock dataBlock = new DataBlock(tempRegisters.get(type), type);
//            addQueue(dataBlock);
//        }
//        warehouseReport.setEndTime(new Date());
//        // 不管是否有异常 都要调用commit操作和输出线程的commit
//        this.commitFlag = true;
//        // 设置commitFlag为true后 需要等待异步输出线程结束后
//        /**
//         *  explain:
//         *  <pre>
//         *  	加上"this.distributeThread != null， 是为了
//         *  	防止当解码一个连50行都没有，就出错的文件时distributeThread线程还未启动，
//         *  	那么在此处，就不能傻等，一直挂死在这儿。
//         *  	ps.	distributeThread启动，目前代码是在addQueue中启动的，
//         *  		但当exceptionFlg为true时，就continue了。
//         *  </pre>
//         */
//        while (!distributedFlag && this.distributeThread != null) {
//            ThreadUtil.sleep(500);
//        }
//        for (Exporter export : this.exports) {
//            // 只需要关闭没有报错的Exporter。报错的Exporter自动会关闭
//            if (!errorExporters.containsKey(export.getExportId())) {
//                export.getCacher().commit();
//            }
//        }
//    }
//
//    /**
//     * 将DataBlock添加至传输队列中
//     *
//     * @param dataBlock
//     */
//    void addQueue(DataBlock dataBlock) {
//        // 分发线程延迟启动 避免未解码出数据 线程启动后不关闭
//        // 1、即在解码线程解出第一个DataBlock启动
//        // 2、解码条数不足一个DataBlock时则在commit时启动
//        if (this.distributeThread == null) {
//            distributeThread = new DistributeThread();
//            distributeThread.start();
//        }
//        try {
//            lock.lockInterruptibly();
//            while (dataQueue.size() == this.maxNum) {
//                full.await();
//            }
//            this.dataQueue.add(dataBlock);
//            empty.signalAll();
//        } catch (InterruptedException e) {
//            full.signalAll();
//            log.error("解码线程获取Lock异常", e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    @Override
//    public void rollBack() {
//        new UnsupportedClassVersionError("不支持回滚");
//    }
//
//    @Override
//    public WarehouseReport getReport() {
//        warehouseReport.setTotal(total);
//        warehouseReport.setSucc(succ);
//        warehouseReport.setFail(fail);
//        warehouseReport.setCause(cause);
//        warehouseReport.setDistributedNum(getDistributeNum());
//        // 20151105 add by tyler.lee for TableReport begin
//        Map<String, WarehouseReport.TableReport> tableInfo = new HashMap<String, WarehouseReport.TableReport>();
//        for (Exporter export : this.exports) {
//            AbstractExporter ae = (AbstractExporter) export;
//            WarehouseReport.TableReport tr = new WarehouseReport.TableReport();
//            tr.setDataType(ae.getDataType());
//            tr.setTableName(ae.getDest());
//            tr.setCause(ae.getCause());
//            tr.setStartTime(ae.getStartTime());
//            tr.setEndTime(ae.getEndTime());
//            tr.setFail(ae.getFail());
//            tr.setSucc(ae.getSucc());
//            tr.setTotal(ae.getTotal());
//            tableInfo.put(ae.getDest(), tr);
//        }
//        warehouseReport.setTableInfo(tableInfo);
//        // 20151105 add by tyler.lee for TableReport end
//        return warehouseReport;
//    }
//
//    /**
//     * 获取已经输出的条数
//     *
//     * @return 已经分发至warehouse的条数
//     */
//    protected String getDistributeNum() {
//        if (distributeNum.isEmpty())
//            return null;
//        StringBuilder distributedNum = new StringBuilder();
//        Set<String> keys = distributeNum.keySet();
//        for (String num : keys) {
//            distributedNum.append(num).append(":").append(distributeNum.get(num)).append(";");
//        }
//        return distributedNum.toString();
//    }
//
//    /**
//     * 异步分发线程
//     *
//     * @author chenrongqiang @ 2013-4-14
//     */
//    class DistributeThread extends Thread {
//
//        public void run() {
//            log.debug("TaskId={},BufferedMultiExportRepository异步分发线程启动。", taskInfo.getId());
//            int distributedNum = 0;
//            // 如果没有提交或者临时队列中仍然有数据 则线程一直运行
//            DataBlock dataBlock = null;
//            while (true) {
//                try {
//                    lock.lockInterruptibly();
//                    while (dataQueue.isEmpty() && !commitFlag) {
//                        empty.awaitNanos(100000000L);
//                    }
//                    if (!dataQueue.isEmpty()) {
//                        dataBlock = dataQueue.remove(0);
//                        full.signal();
//                    }
//                } catch (InterruptedException e) {
//                    log.error("TaskId={},BufferedMultiExportRepository异步分发异常。", taskInfo.getId(), e);
//                } finally {
//                    lock.unlock();
//                }
//                /**
//                 * <pre>
//                 * 修改说明：
//                 * 	这个地方要检测一下 dataBlock是否为null，
//                 * 	否则commitFlag=true且dataQueue.isEmpty()时将会出错.
//                 * 	distribute完后，dataBlock要置为null，否则按原先模式会造成最后一个dataBlock重复入库(1-N次)
//                 * </pre>
//                 */
//                if (dataBlock != null) {
//                    distribute(dataBlock);
//                    distributedNum++;
//                    dataBlock = null;
//                }
//                if (commitFlag && dataQueue.isEmpty())
//                    break;
//            }
//            // 将distributedFlag标记置为true，保证在输出完成后才commit 缓存
//            distributedFlag = true;
//            log.debug("TaskId={},BufferedMultiExportRepository异步分发线程完成,共分发数据块{}", new Object[]{taskInfo.getId(), distributedNum});
//        }
//    }
//}
