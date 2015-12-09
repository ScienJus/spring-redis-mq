package com.scienjus.queue.producer.impl;

import com.scienjus.queue.producer.Producer;
import com.scienjus.queue.util.JedisUtil;
import com.scienjus.queue.util.Message;
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

    public void sendMessage(String topic, Message message) {
        JedisUtil.lpush(jedisPool, topic, message);
    }

}
