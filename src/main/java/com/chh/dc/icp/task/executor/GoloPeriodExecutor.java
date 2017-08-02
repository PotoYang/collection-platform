package com.chh.dc.icp.task.executor;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.task.TaskFuture;
import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fulr
 */
public class GoloPeriodExecutor extends PeriodExecutor {


    private static Logger LOG = LoggerFactory.getLogger(GoloPeriodExecutor.class);

//    static{
//        //元征采集账户注册
//        GoloUtils.regUser();
//    }

    public GoloPeriodExecutor(TaskInfo taskInfo) {
        super(taskInfo);
        // TODO Auto-generated constructor stub
    }


    /**
     * 需要每次都调用accessor的getData来获取下一个设备数据，直到任务中所有设备数据采集完毕
     */
    @Override
    protected void exec(TaskFuture future) throws Exception {
        LOG.info(logHead + "开始执行任务");
        boolean accessResult = accessor.access();
        if (!accessResult) {
            LOG.warn(logHead + "采集器访问失败");
            return;
        }
        DataPackage data = null;
        LOG.info(logHead + "采集器访问成功，开始解析");
        while ((data = accessor.getData()) != null) {
            try {
//                data = accessor.getData();
//                if (data == null) {
//                    continue;
//                }
                parser.parse(taskInfo, data);
                ParsedRecord record = null;
                while ((record = parser.readRecord()) != null) {
                    repository.transport(record);
                }
//                ParsedRecord record = parser.readRecord();
//                //如果解析到的数据不是需要的，则解析器会返回空
//                if (record == null) {
//                    continue;
//                }
//                repository.transport(record);
            } catch (Exception e) {
                LOG.error(logHead + "解析数据源出错:" + data.getDesc(), e);
            }
        }
        repository.commit();
        LOG.info(logHead + "任务采集解析完毕");
    }
}
