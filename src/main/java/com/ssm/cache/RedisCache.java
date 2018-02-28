package com.ssm.cache;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class RedisCache implements Cache {

    //jedis客户端工厂连接对象
    public static JedisConnectionFactory connection;

    /**
     * 外部传递过来连接工厂对象
     **/
    public static void setConnection(JedisConnectionFactory connection) {
        RedisCache.connection = connection;
    }

    //定义一个缓存的id变量
    private String id;

    //定义一个读写锁
    private ReadWriteLock readWriteLock
            = new ReentrantReadWriteLock();

    public RedisCache(String id) throws Exception {
        if (id == null) {
            throw new Exception("需要传递缓存id");
        }
        this.id = id;
    }

    /**
     * 当前二级缓存的唯一id
     **/
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * 往二级缓存中存入对象
     *
     * @param key   存储的对象key
     * @param value 存储的具体值
     **/
    @Override
    public void putObject(Object key, Object value) {

        //获得jedis缓存连接对象
        JedisConnection con = null;

        try {
            con = connection.getConnection();

            //获取jdk序列化对象，用于存储redis对象的序列化转换
            RedisSerializer<Object> serializer = new
                    JdkSerializationRedisSerializer();

            //序列化对象
            byte[] keys = serializer.serialize(key);
            byte[] values = serializer.serialize(value);

            //将数据对象保存到redis缓存中
            con.set(keys, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (con != null) {
                con.close();
            }
        }
    }

    /**
     * 通过key获取缓存中的对象值
     *
     * @param key 要获取的对象key
     **/
    @Override
    public Object getObject(Object key) {
        JedisConnection con = null;

        try {
            con = connection.getConnection();

            RedisSerializer<Object> serializer = new
                    JdkSerializationRedisSerializer();

            byte[] keys = serializer.serialize(key);

            byte[] values = con.get(keys);

            //将返回的value值转换为object对象
            return serializer.deserialize(values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return null;
    }

    /**
     * 移出某个key
     *
     * @param key 要移出的key
     **/
    @Override
    public Object removeObject(Object key) {
        JedisConnection con = null;

        try {
            con = connection.getConnection();

            RedisSerializer<Object> serializer = new
                    JdkSerializationRedisSerializer();

            byte[] keys = serializer.serialize(key);

            //让某个key在几秒之后失效，如果是0则表示立即失效
            return con.expire(keys, 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }


        return null;
    }

    /**
     * 清空缓存
     **/
    @Override
    public void clear() {
        JedisConnection con = null;

        try {
            con = connection.getConnection();
            //刷新缓存
            con.flushDb();
            con.flushAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

    }

    /**
     * 返回当前缓存条数
     **/
    @Override
    public int getSize() {
        JedisConnection con = null;

        try {
            con = connection.getConnection();

            //返回当前缓存大小
            return con.dbSize().intValue();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return 0;
    }

    /**
     * 返回当前缓存对象的读写锁
     **/
    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }
}
