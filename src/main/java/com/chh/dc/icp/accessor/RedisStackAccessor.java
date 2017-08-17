package com.chh.dc.icp.accessor;

import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.util.redis.JedisFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by Niow on 2016/7/12.
 */
public class RedisStackAccessor extends AbstractAccessor {

    public static final Logger log = LoggerFactory.getLogger(RedisStackAccessor.class);

    private Jedis jedis = null;

    private byte[] stackName = null;

    @Override
    public boolean access() {
        jedis = JedisFactory.createJedis(taskInfo.getHostAddr(), taskInfo.getPort(), taskInfo.getCollectTimeoutSec(), taskInfo.getPassword());
        stackName = taskInfo.getCollectPath().getBytes();
        log.info("接入Redis[{}:{}][{}],timeout:{},password:{}", taskInfo.getHostAddr(), taskInfo.getPort(), taskInfo.getCollectPath(), taskInfo.getCollectTimeoutSec(), StringUtil.isNull(taskInfo.getPassword()) ? null : taskInfo.getPassword().replaceAll("/*", "*"));
        return true;
    }

    @Override
    public DataPackage getData() {
        byte[] rpop = jedis.rpop(stackName);
        if (rpop == null) {
            return null;
        }
        DataPackage data = new DataPackage();
        data.setData(rpop);
        return data;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
