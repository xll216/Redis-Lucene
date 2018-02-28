package com.ssm.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class RedisCacheTransfer {

    /*通过spring中的set方法进行注入*/
    @Autowired
    public void setJedisConnectionFactory(
            JedisConnectionFactory factory) {
        RedisCache.connection = factory;
    }
}
