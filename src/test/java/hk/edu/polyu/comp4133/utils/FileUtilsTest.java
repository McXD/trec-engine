package hk.edu.polyu.comp4133.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    @Test
    void splitFile() throws IOException {
        String path = "src/test/resources/post.txt";
        int nParts = 50;

        long[] offsets = FileUtils.splitFile(path, nParts);
        RandomAccessFile file = new RandomAccessFile(path, "r");

        System.out.println(file.readLine());
        for (int i = 0; i < nParts-1; i++) {
            file.seek(offsets[i]);
            System.out.println(file.readLine());
        }
    }


    @Test
    public void test() {
        System.out.println("test".length());
    }
}