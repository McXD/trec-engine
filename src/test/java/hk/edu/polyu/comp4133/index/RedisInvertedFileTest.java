package hk.edu.polyu.comp4133.index;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class RedisInvertedFileTest {
    @Test
    void build() throws IOException, InterruptedException {
        RedisInvertedFile invertedFile = new RedisInvertedFile("localhost", 6380);
        invertedFile.build(10, "src/test/resources/post.txt");
    }

//    @Test
    void load() {
        RedisInvertedFile inv = new RedisInvertedFile("localhost", 6380);
        System.out.println(inv.getPostingList("shoP"));
        System.out.println(inv.getDocLength(41));
    }

    @Test
    void calc() throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(20);
        RedisInvertedFile inv = new RedisInvertedFile("localhost", 6379);
        double corpusSize = 64813;
        Jedis jedis = inv.jedisPool.getResource();
        Set<String> keys = jedis.keys("freq:*");
        List<Callable<Void>>  tasks = new ArrayList<>();
        for (List<String> partition : Iterables.partition(keys, 3000)) {
            tasks.add(inv.calcDocLengthPartTask(corpusSize, new HashSet<>(partition)));
        }
        es.invokeAll(tasks);
    }
}