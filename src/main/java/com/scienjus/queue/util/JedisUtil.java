package com.scienjus.queue.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author XieEnlong
 * @date 2015/12/9.
 */
public class JedisUtil {

    public static void lpush(JedisPool jedisPool, String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key.getBytes(), SerializeUtil.serialize(value));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Object rpop(JedisPool jedisPool, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return SerializeUtil.unserialize(jedis.rpop(key.getBytes()));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
