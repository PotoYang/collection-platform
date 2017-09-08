package com.chh.dc.icp.task.executor;


import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.task.TaskFuture;
import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceExecutor extends AbstractExecutor {

    private static Logger LOG = LoggerFactory.getLogger(CommonExecutor.class);

    private Boolean runningFlag = true;

    public ServiceExecutor(TaskInfo taskInfo) {
        // TODO Auto-generated constructor stub
        super(taskInfo);
    }

    /**
     * 服务类型的执行器，需要每次都调用accessor的getData来获取吓一跳数据
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
        while (runningFlag) {
            try {
                data = accessor.getData();
                if (data == null) {
                    continue;
                }
                parser.parse(taskInfo, data);
                ParsedRecord record = null;
                //一个数据包中出现了多种数据类型和多条数据的情况，需要循环读取
                while ((record = parser.readRecord()) != null) {
                    repository.transport(record);
                }
//                //如果解析到的数据不是需要的，则解析器会返回空
//                if (record == null) {
//                    continue;
//                }
//                repository.transport(record);
            } catch (Exception e) {
                LOG.error(logHead + "解析数据源出错:" + data.getDesc(), e);
            }
        }
        // forever not here?
        repository.commit();
        LOG.info(logHead + "任务采集解析完毕");
    }

    @Override
    protected void afterExec(TaskFuture future) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop() {
        this.runningFlag = false;
    }
}
