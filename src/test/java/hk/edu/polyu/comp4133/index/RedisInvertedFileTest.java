package hk.edu.polyu.comp4133.index;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class RedisInvertedFileTest {
    @Test
    void build() throws IOException, InterruptedException {
        RedisInvertedFile invertedFile = new RedisInvertedFile("localhost", 6379);
        invertedFile.build(10, "src/test/resources/post.txt");
    }

    @Test
    void load() {
        RedisInvertedFile inv = new RedisInvertedFile("localhost", 6379);
        System.out.println(inv.getPostingList("shoP"));
        System.out.println(inv.getDocLength(41));
    }
}