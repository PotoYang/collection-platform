package com.chh.dc.icp.warehouse;


import com.chh.dc.icp.warehouse.repository.Repository;
import com.chh.dc.icp.warehouse.repository.RepositoryArgs;

public interface WareHouse {

    /**
     * 申请仓库 判断是否还有仓库容量
     *
     * @return 仓库ID
     */
    boolean isWarehouseReady(Long taskId);

    /**
     * 通知warehouse 当前任务已经申请一个仓库
     *
     * @param taskId
     */
    void applyNotice(Long taskId);

    /**
     * 通知warehouse 当前任务已经关闭一个仓库
     */
    void shutdownNotice(Long taskId);

    /**
     * 申请仓库
     *
     * @return 仓库对象
     */
    Repository apply(RepositoryArgs repositoryArgs) throws Exception;

    /**
     * 往仓库中写入数据
     *
     * @param reposId
     * @param parsedRecord
     * @return
     */
    int transport(long reposId, ParsedRecord parsedRecord);

    /**
     * 提交数据
     *
     * @param reposId
     */
    void commit(long reposId, boolean exceptionFlag);

    /**
     * 数据回滚
     *
     * @param reposId
     */
    void rollBack(long reposId);

    /**
     * 获取仓库数据处理报告
     *
     * @param reposId
     */
    WarehouseReport getReport(long reposId);

    /**
     * 关闭指定仓库
     *
     * @param reposId
     */
    void close(long reposId);

    /**
     * 停止仓库服务
     */
    void shutdown();
}
