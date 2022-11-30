package hk.edu.polyu.comp4133.utils;

import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils {
    public static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
