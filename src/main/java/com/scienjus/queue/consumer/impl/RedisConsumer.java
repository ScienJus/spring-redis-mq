package com.scienjus.queue.consumer.impl;

import com.scienjus.queue.consumer.Consumer;
import com.scienjus.queue.util.SerializeUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class RedisConsumer implements Consumer {

    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Object getMessage(String topic) {
        return rpop(topic);
    }

    private Object rpop(String topic) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return SerializeUtil.unserialize(jedis.rpop(topic.getBytes()));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
