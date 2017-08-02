package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.Runner;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.util.DBUtil;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;
import com.chh.dc.icp.warehouse.exporter.template.db.DBExportTemplate;
import com.chh.dc.icp.warehouse.exporter.template.db.DBTarget;
import com.chh.dc.icp.warehouse.exporter.template.db.TableTemplate;
import com.chh.dc.icp.util.TimeUtil;
import com.chh.dc.icp.warehouse.DataBlock;
import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;
import com.chh.dc.icp.warehouse.exporter.template.db.ColumnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.sql.*;
import java.util.Date;
import java.util.*;


/**
 * 使用JDBC批量提交的数据库输出器
 *
 * @version 1.0
 * @since 3.0
 */
public class JDBCBatchExporter extends AbstractBufferedExporter {

    /**
     * 日志
     */
    protected static final Logger log = LoggerFactory.getLogger(JDBCBatchExporter.class);

    /**
     * 默认批次条数
     */
    protected static final int DEFAULT_BATCH_NUM = 1000;

    /**
     * 每多少条向数据库提交一次。
     */
    protected int batchNum;

    /**
     * 记录当前已缓存了多少条。
     */
    protected int currNum;

    /**
     * 数据库输出模版
     */
    protected DBExportTemplate dbExportTemplate;

    /**
     * 输出字段列表
     */
    protected List<ColumnTemplate> columns;

    /**
     * 数据库链接 在DBExporter初始化的时候创建 并且在异常或者数据分发完成后关闭
     */
    protected Connection con;

    protected PreparedStatement statement;

    /**
     * 执行SQL语句
     */
    protected String sql;

    /**
     * 数据库输出类型
     */
    protected String storageType;

    /**
     * 输出表名
     */
    protected String table;

    /**
     * 输出总时间
     */
    protected long totalTime;

    protected SQLException lastSQLException;

    /**
     * Map<列索引，列类型> 对于入库一个表， 有多条记录出错时， 只查询一次表结构获取数据类型， 没有必要每次
     **/
    protected Map<Integer, Integer> columnMappingMap = new HashMap<Integer, Integer>();


    /**
     * 构造函数
     */
    public JDBCBatchExporter(ExportTemplate template, TaskInfo taskInfo) throws Exception {
        super(template, taskInfo);

        this.exportId = template.getId();
        this.dataType = template.getDataType();
        this.dbExportTemplate = (DBExportTemplate) template;
        this.dest = dbExportTemplate.getTable().getName();
        this.exportType = 1;
        this.columns = dbExportTemplate.getColumns();
        if (TableTemplate.OPTION_UPDATE.equals(dbExportTemplate.getTable().getOption())) {
            this.sql = dbExportTemplate.getTable().getSql();
        } else {
            createInsertSql();
        }
        initJdbc();
        log.debug("JDBCBatch输出器初始化完成:目的地=" + dbExportTemplate.getTable().getName() + ",输出开始断点=" + this.breakPoint);
    }

    /**
     * 创建数据库链接
     */
    protected void initJdbc() throws Exception {

        try {
            String targetBean = dbExportTemplate.getTarget();
            DBTarget dbTarget = Runner.getBean(targetBean, DBTarget.class);
            Class.forName(dbTarget.getDriver());
            this.con = DriverManager.getConnection(dbTarget.getUrl(), dbTarget.getUsername(), dbTarget.getPassword());
            this.batchNum = dbExportTemplate.getBatchNumber();
            // 如果批量入库条数非法[目前限制最大10000]
            if (batchNum <= 0 || batchNum > 10000) {
                batchNum = DEFAULT_BATCH_NUM;
            }
            log.debug("exportId：{},每次批量入库条数为：{}", new Object[]{dbExportTemplate.getId(),batchNum});
            this.statement = con.prepareStatement(this.sql);
            currNum = 0;
        } catch (Exception e) {
            exporterInitErrorFlag = true;
            // -1表示初始化失败
            this.errorCode = -1;
            this.cause = e.getMessage();
//            log.error("exportId：{},数据入库时，数据库连接创建失败。",dbExportTemplate.getId(), e);
            throw new Exception("exportId："+dbExportTemplate.getId()+",数据入库时，数据库连接创建失败。",e);
        }
    }

    protected void checkConnection() throws Exception {
       if (!accessAble()) {
           initJdbc();
       }
    }

    protected boolean accessAble(){
        try {
            if (statement == null || statement.isClosed()||con==null||con.isClosed()||con.isReadOnly()){
                return false;
            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    protected String createCacherName() {
        return taskInfo.getId() + "" + UUID.randomUUID().toString();
    }


    @Override
    protected void doRealExport(DataBlock dataBlock) throws Exception {
        List<ParsedRecord> outData = dataBlock.getData();
        if (outData == null || outData.isEmpty()) {
            return;
        }
        checkConnection();
        for (ParsedRecord outRecord : outData) {
            Map<String, Object> record = outRecord.getRecord();
            export(record);
        }
    }

    // 这里入库用的PreparedStatement，即“绑定变量”方式的SQL语句，如果是普通的Statement，
    // 那么对于数据库，每条记录都是一条全新的SQL语句，会非常慢，并且数据库吃不消。
    // 提交方式是批量的，即在本地缓存一定数量的记录，再提交，这样会快很多。
    // 如果采用每条都executeUpdate的方式，那么每条记录都有一次网络IO，数据库也提交一次，
    // 这样非常的慢，并且网络和数据库的负荷也极大。
    // 但批量提交的缺点是一批记录中如果有一条出错，那么这整批记录都会入库失败。
    private void export(Map<String, Object> record) throws Exception {
        long start = System.currentTimeMillis();
        this.current++;
        try {
            int colNum = this.columns.size();
            int inIndex = 0;
            for (int i = 0; i < colNum; i++) {
                ColumnTemplate column = this.columns.get(i);
                String prop = column.getProperty();
                //如果对应record属性名没写，则默认是和数据库字段名相同
                if (StringUtil.isNull(prop)) {
                    prop = column.getName();
                }
                Object value = record.get(prop);
                if (column.getIsSpan() != null && column.getIsSpan().equalsIgnoreCase("true") && column.getDefaultValue() != null) {
                    // 如果字段的默认值作为sql拼接，则跳过对这个字段的赋值
                    continue;
                }
                if (value == null) {
                    if (column.getDefaultValue() != null) {
                        statement.setObject(++inIndex, column.getDefaultValue());
                    } else {
                        statement.setObject(++inIndex, value);
                    }
                    continue;
                }
                if (value.toString().equalsIgnoreCase("nan") || StringUtil.isNull(value.toString())) {
                    value = null;
                }
                if (column.getFormat() == null) {
                    statement.setObject(++inIndex, value);
                    continue;
                }
                if (column.getFormat().equals("blob")) {// 处理大字段blob
                    statement.setBlob(++inIndex, handleBlob(value.toString()));
                    continue;
                }
                if (column.getFormat().equals("Timestamp")) {// 处理Timestamp
                    statement.setTimestamp(++inIndex, new Timestamp(TimeUtil.getDate(value.toString()).getTime()));
                    continue;
                }
                statement.setObject(++inIndex, value);
            }
            statement.addBatch();
            this.currNum++;
            if (this.currNum >= batchNum) {
//                this.currNum = 0;
                statement.executeBatch();
//                DBUtil.close(null, statement, null);
//                statement = con.prepareStatement(this.sql);
//                this.succ += batchNum;
                updateSuccNum();
            }
            long end = System.currentTimeMillis();
            totalTime += (end - start);
        } catch (Exception e) {
            if (e instanceof SQLException) {
                this.lastSQLException = (SQLException) e;
            }
            String errSql = buildErrRecordSql(record);
            // 主键冲突
            if (e instanceof SQLException && e.getMessage().indexOf("ORA-00001") >= 0) {
                log.error("exportId：{},主键冲突：{}", dbExportTemplate.getId(),errSql);
                // ++this.fail;
                // --this.succ;
                // return;
            } else {
                log.error(errSql);
            }
            this.cause = e.getMessage();
//            log.error("数据库输出发生异常,入库线程停止,当前断点=" + breakPoint + ",文件：" + entryNames.get(0) + "，任务：" + taskInfo.getId(), e);
//            this.fail += this.currNum;
//            this.currNum=0;
            updateFailNum(e);
            DBUtil.close(null, statement, con);
            throw new Exception("写入数据库失败：" + table+",失败条数："+currNum, e);
        }
    }


    /**
     * 组装错误SQL BUFFER;
     *
     * @param record 记录集
     * @return sql入库语句
     */
    protected String buildErrRecordSql(Map<String, Object> record) {
        PreparedStatement queryStatement = null;
        ResultSet rs = null;
        try {
            StringBuilder errSqlBuff = new StringBuilder();
            StringBuilder errMsg = null;
            int preQuesTokenPos = 0;
            int currQuesTokenPos = preQuesTokenPos;

            //当出现错误时， 需要获取表结构的数据类型, 只需要获取一次即可, 方便输出日志记录， 解决问题： 针对主键冲突以及字段类型错误， 照成数据库io比较大
            if (columnMappingMap.size() == 0) {
                String querySql = buildQuerySql();
                queryStatement = con.prepareStatement(querySql);
                rs = queryStatement.executeQuery();
                ResultSetMetaData metaData = rs.getMetaData();
                int cCount = metaData.getColumnCount();
                // 模板里面的列索引 和表结构的列索引建立对应关系
                for (int i = 0; i < cCount; i++) {
                    int columnType = metaData.getColumnType(i + 1);
                    columnMappingMap.put(i, columnType);
                }
            }

            int colNum = this.columns.size();
            for (int i = 0; i < colNum; i++) {
                currQuesTokenPos = this.sql.indexOf("?", preQuesTokenPos);
                if (currQuesTokenPos < 0)
                    break;

                errSqlBuff.append(this.sql.substring(preQuesTokenPos, currQuesTokenPos));
                // 偏移一个"?"号的位置
                preQuesTokenPos = currQuesTokenPos + 1;

                ColumnTemplate column = this.columns.get(i);
                String prop = column.getProperty();
                Object value = record.get(prop);

                if (value == null || "".equals(value) || value.toString().equalsIgnoreCase("nan")) {
                    errSqlBuff.append("null");
                } else {
                    int columnType = columnMappingMap.get(i);

                    if (java.sql.Types.CHAR == columnType || java.sql.Types.NCHAR == columnType || java.sql.Types.VARCHAR == columnType
                            || java.sql.Types.NVARCHAR == columnType || java.sql.Types.DATE == columnType || java.sql.Types.TIMESTAMP == columnType) {
                        errSqlBuff.append("'").append(value).append("'");
                    } else {
                        Character invalidChar = null;
                        String valueTem = value.toString().trim();

                        if (java.sql.Types.BIGINT == columnType || java.sql.Types.DOUBLE == columnType || java.sql.Types.FLOAT == columnType
                                || java.sql.Types.DECIMAL == columnType || java.sql.Types.INTEGER == columnType
                                || java.sql.Types.NUMERIC == columnType) {
                            for (int charIndex = 0; charIndex < valueTem.length(); ++charIndex) {
                                char c = valueTem.charAt(charIndex);

                                if (!((c == '-' && charIndex == 0) || ((c == '.' || c == 'E' || c == 'e') && charIndex > 0) || (c >= '0' && c <= '9'))) {
                                    invalidChar = c;
                                    break;
                                }
                            }
                        }

                        if (invalidChar != null) {
                            if (errMsg == null) {
                                errMsg = new StringBuilder();
                                errMsg.append("TABLE:").append(this.dest).append(" 值类型与数据库类型不一致，数据库为数字类型，但值中包含非数字字符.\n");
                            }

                            errMsg.append("column: ").append(column.getName()).append(" value:").append(value).append(" invalid char: [")
                                    .append(invalidChar.toString()).append("] \n");
                        }

                        errSqlBuff.append(value);
                    }
                }
            }

            if (preQuesTokenPos < this.sql.length())
                errSqlBuff.append(this.sql.substring(preQuesTokenPos, this.sql.length()));

            if (errMsg != null)
                return errMsg.toString();

            return errSqlBuff.toString();
        } catch (SQLException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage()).append("\n");
            sb.append("sql语句：").append(this.sql).append("\n 数据行：");
            int colNum = this.columns.size();
            for (int i = 0; i < colNum; i++) {
                ColumnTemplate column = this.columns.get(i);
                String prop = column.getProperty();
                Object value = record.get(prop);
                if (value != null && value.toString().equalsIgnoreCase("nan")) {
                    value = null;
                }
                sb.append((value != null ? "'" + value + "'" : "null")).append(",");
            }

            return sb.toString();
        } finally {
            DBUtil.close(rs, queryStatement, null);
        }

    }


    @Override
    public void close() {
        long start = System.currentTimeMillis();
        try {
            // 可能还有未提交的内容，最后要提交一下。
            /**
             * <pre>
             *  dissable:shig date:2014-5-22
             *  explain:statement.isClosed()调用会因为找不到方法，报错，
             *  现改为，close过后，将statement置为null，通过检查statement是否为null，来替代调用isClosed()方法
             * </pre>
             */
            if (statement != null /* && !statement.isClosed() */ && this.total > 0) {
                statement.executeBatch();
            }
//            this.succ += this.currNum;
            this.breakPoint += this.currNum;
            updateSuccNum();

        } catch (Exception e) {
//            this.fail += this.currNum;
            try {
                // 未关闭自动事务时，批量入库遇到无法插入的语句时，之前成功的还是会入库，通过getUpdateCount()可获取成功条数。
                this.succ += this.statement.getUpdateCount();
            } catch (SQLException ex) {
            }
//            log.error(this.table + "  , endExport导入数据库失败", e);
            updateFailNum(e);
        } finally {
            DBUtil.close(null, statement, con);
            statement = null;
            endTime = new Date();

            // 优先根据config.ini中的system.logcltinsert.flag进行判断，如果开启（1）， 添加日志入库操作 log_clt_insert ,数据库汇总需要依赖此表,
            // 话单入 log_cdr_insert, 则需要将 system.logcltinsert.flag=0 关闭.
            if (isLogCltInsertFlag()) {
                logCltInsert();
            } else {
                if (this.dbLoggerFlag) {
                    writeDbLog();
                }
            }

            long end = System.currentTimeMillis();
            totalTime += (end - start);
            log.debug("【入库时间统计】{}表入库耗时{}秒，入库成功{}条数据，jdbc入库失败{}条数据，{}任务，原始文件：{}，CITY：{}，OMC：{}，BSC：{}，VENDOR：{}", new Object[]{this.table,
                    totalTime / 1000.00, this.succ, this.fail, taskInfo.getId()});
        }
    }

    private void writeDbLog() {

    }

    @Override
    public void endExportOnException() {
        close();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public String getDataType() {
        return dataType;
    }

    @Override
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    /**
     * 初始化输出SQL
     *
     * @return
     */
    protected void createInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(dbExportTemplate.getTable().getName()).append(" (");
        this.table = dbExportTemplate.getTable().getName();

        String jdbcDriver = taskInfo.getDbDriver();

        boolean isMySqlDB = false;
        if (jdbcDriver.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
            isMySqlDB = true;
        }

        StringBuilder sbValue = new StringBuilder();
        int colNum = this.columns.size();
        for (int i = 0; i < colNum; i++) {
            ColumnTemplate column = this.columns.get(i);
            sb.append(column.getName());
            if (i < colNum - 1) {
                sb.append(",");
            }
            String fmt = column.getFormat();
            if (fmt != null && !fmt.trim().isEmpty() && !fmt.equals("blob")) {
                if (isMySqlDB) {
                    sbValue.append("?");
                } else {
                    sbValue.append("to_date(?,'").append(fmt).append("')");
                }
            } else
                sbValue.append("?");

            if (i < colNum - 1)
                sbValue.append(",");
        }
        sb.append(") values (").append(sbValue.toString()).append(")");
        this.sql = sb.toString();
        if (log.isDebugEnabled()) {
            log.debug("任务:[{}]生成输出SQL:{}", taskInfo.getName(), this.sql);
        }
    }

    /**
     * 初始化输出SQL
     *
     * @return
     */
    protected String buildQuerySql() {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");

        int colNum = this.columns.size();
        for (int i = 0; i < colNum; i++) {
            ColumnTemplate column = this.columns.get(i);

            sb.append(column.getName());
            if (i < colNum - 1)
                sb.append(",");
        }
        sb.append(" from ").append(dbExportTemplate.getTable().getName());
        // sb.append(" where rownum < 1");
        sb.append(" where 1=0");

        return sb.toString();
    }

    /**
     * 处理blob字段
     *
     * @param value
     * @return
     */
    protected Blob handleBlob(String value) {
        Blob blob = null;
        try {
            blob = this.con.createBlob();
            OutputStream out = blob.setBinaryStream(1);
            out.write(value.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("blob字段处理异常", e);
        }
        return blob;
    }

    @Override
    public void buildExportPropertysList(Set<String> propertysSet) {
        int colNum = this.columns.size();
        for (int i = 0; i < colNum; i++) {
            ColumnTemplate column = this.columns.get(i);
            String prop = column.getProperty();
            propertysSet.add(prop);
        }
    }

    @Override
    public void commit() {
        super.commit();
        try {
            if (currNum == 0) {
                return;
            }
            checkConnection();
            statement.executeBatch();
//            DBUtil.close(null, statement, null);
//            statement = con.prepareStatement(this.sql);
//            this.succ += currNum;
//            log.info("exportId：{}，提交了类型为{}的数据{}条", dbExportTemplate.getId(),dataType, this.currNum);
//            this.currNum = 0;
            updateSuccNum();
        } catch (Exception e) {

//            log.error("exportId：{}，{}条数据提交失败", dbExportTemplate.getId(),currNum,e);
            updateFailNum(e);
            DBUtil.close(null, statement, con);
        }
    }

    public SQLException getLastSQLException() {
        return this.lastSQLException;
    }

    private void updateSuccNum(){
        log.info("exportId：{}，提交了类型为{}的数据{}条", dbExportTemplate.getId(),dataType, this.currNum);
        this.succ += currNum;
        this.currNum=0;
    }

    private void updateFailNum(Exception e){
        log.error("exportId：{}，{}条数据提交失败", dbExportTemplate.getId(),currNum,e);
        this.fail += currNum;
        this.currNum=0;
    }
}
