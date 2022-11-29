package hk.edu.polyu.comp4133.index;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

class FilePostInputStreamTest {
    @Test
    void canReadPosts() throws FileNotFoundException {
        FilePostInputStream stream = new FilePostInputStream("src/test/resources/post.txt");
        PostInputStream.PostEntry entry;
        while ((entry = stream.next()) != null) {
            System.out.println(entry);
        }
    }
}