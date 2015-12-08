package com.scienjus.queue.producer.impl;

import com.scienjus.queue.producer.Producer;
import com.scienjus.queue.util.SerializeUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class RedisProducer implements Producer {

    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void sendMessage(String topic, Object message) {
        lpush(topic, message);
    }

    private void lpush(String topic, Object message) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(topic.getBytes(), SerializeUtil.serialize(message));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
