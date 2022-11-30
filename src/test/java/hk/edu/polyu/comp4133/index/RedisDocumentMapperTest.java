package hk.edu.polyu.comp4133.index;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class RedisDocumentMapperTest {
    @Test
    void testIndex() throws IOException, InterruptedException {
        RedisDocumentMapper mapper = new RedisDocumentMapper("localhost", 9999);
        mapper.index("src/test/resources/file.txt", 5);
    }

    @Test
    void testMap() {
        RedisDocumentMapper mapper = new RedisDocumentMapper("localhost", 9999);
        System.out.println(mapper.map(0));
    }
}