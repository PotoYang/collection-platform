package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.Runner;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.util.SerializeUtil;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.util.redis.JedisFactory;
import com.chh.dc.icp.warehouse.ParsedRecord;
import com.chh.dc.icp.warehouse.exporter.template.redis.OptionTemplate;
import com.chh.dc.icp.warehouse.DataBlock;
import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;
import com.chh.dc.icp.warehouse.exporter.template.redis.RedisExportTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by Niow on 2016/9/28.
 */
public class RedisExporter extends AbstractBufferedExporter {

    public static final Logger log = LoggerFactory.getLogger(RedisExporter.class);

    public static final String OP_ZADD = "zadd";

    public static final String OP_SADD = "sadd";

    public static final String OP_PUBLISH = "publish";

    public static final String OP_LPUSH = "lpush";

    public static final String OP_SET = "set";

    public static final String OP_HASH = "hash";

    private JedisFactory jedisFactory;

    private Jedis jedis;

    private RedisExportTemplate template;

    private byte[] key;

    private List<String> replaceKey;

    private OptionTemplate option;

    public RedisExporter(ExportTemplate exportTemplate, TaskInfo taskInfo) {
        super(exportTemplate, taskInfo);
        this.template = (RedisExportTemplate) exportTemplate;
        this.exportId = template.getId();
        this.dataType = template.getDataType();
        option = template.getOptionTemplate();
        jedisFactory = Runner.getBean(exportTemplate.getTarget(), JedisFactory.class);
        if (!this.template.getKey().contains("{")) {
            this.key = this.template.getKey().getBytes();
        } else {
            this.replaceKey = getReplaceKey(this.template.getKey());
        }
    }

    protected List<String> getReplaceKey(String keyValue) {
        String[] split = keyValue.split("\\{");
        List<String> keys = new ArrayList<String>(split.length);
        for (String str : split) {
            int index = str.indexOf("}");
            if (index < 0) {
                continue;
            }
            String dynamicKey = str.substring(0, index);
            keys.add(dynamicKey);
        }
        return keys;
    }

    protected byte[] replaceKey(String key, Map<String, Object> record) {
        String tempKey = key;
        for (String subKey : this.replaceKey) {
            //String value = (String) record.get(subKey);
            Object value = record.get(subKey);
            if(value instanceof Date) {
                value = ((Date)value).getTime();
            }
            if (value == null) {
                log.error("任务[{}][{}]在RedisExporter输出[{}]中替换key[{}]在record中对应值为空", taskInfo.getId(), taskInfo.getName(), template.getDataType(), subKey);
                return null;
            }
            tempKey = tempKey.replace("{" + subKey + "}", value.toString());
        }
        return tempKey.getBytes();
    }


    @Override
    protected void doRealExport(DataBlock dataBlock) throws Exception {
        log.info("redisExporter输出数据[{}]条数[{}]",dataBlock.getDataType(),dataBlock.getData().size());
        List<ParsedRecord> data = dataBlock.getData();
        for (ParsedRecord record : data) {
            Map<String, Object> map = record.getRecord();
            if (this.key != null) {
                operate(this.key, map);
                continue;
            }
            if (replaceKey != null) {
                byte[] keys = replaceKey(this.template.getKey(), map);
                if (keys == null) {
                    continue;
                }
                operate(keys, map);
            }
        }
    }

    protected void operate(byte[] key, Map<String, Object> record) {
        String type = option.getType();
        Jedis jedis = jedisFactory.getJedis();
        byte[] data = null;
        if (!StringUtil.isEmpty(option.getOutput())) {
            data = SerializeUtil.serialize(record.get(option.getOutput()));
        } else {
            data = SerializeUtil.serialize(record);
        }

        try {
            switch (type) {
                case OP_SET: {
                    jedis.set(key, data);
                    break;
                }
                case OP_SADD: {
                    jedis.sadd(key, data);
                    break;
                }
                case OP_ZADD: {
                    Object score = record.get(option.getScore());
                    if (score instanceof Date) {
                        score = ((Date) score).getTime();
                    } else if (score instanceof Double) {
                        jedis.zadd(key, (double) score, data);
                        break;
                    }
                    jedis.zadd(key, Double.parseDouble(score.toString()), data);
                    break;
                }
                case OP_LPUSH: {
                    jedis.lpush(key, data);
                    break;
                }
                case OP_PUBLISH: {
                    jedis.publish(key, data);
                    break;
                }
                case OP_HASH: {
                    Object field = record.get(option.getField());
                    jedis.hset(key,field.toString().getBytes() ,data);
                    break;
                }
            }
            if (option.getTimeout() != 0) {
                jedis.expire(key, option.getTimeout());
            }
        } finally {
            jedisFactory.returnBackJedis(jedis);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void commit() {

    }

    @Override
    public void endExportOnException() {

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
        return this.dataType;
    }

    @Override
    public void setDataType(String dataType) {

    }

    @Override
    public void buildExportPropertysList(Set<String> propertysSet) {

    }


}
