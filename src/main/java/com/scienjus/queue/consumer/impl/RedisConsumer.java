package com.scienjus.queue.consumer.impl;

import com.scienjus.queue.consumer.Consumer;
import com.scienjus.queue.util.JedisUtil;
import com.scienjus.queue.util.Message;
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

    @Override
    public Message getMessage(String topic) {
        return (Message) JedisUtil.rpop(jedisPool, topic);
    }

    @Override
    public void retry(String topic, Message message) {
        message.setFailureTimes(message.getFailureTimes() + 1);
        JedisUtil.lpush(jedisPool, topic, message);
    }
}
