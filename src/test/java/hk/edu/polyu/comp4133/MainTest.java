package hk.edu.polyu.comp4133;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void retrieveAll() throws IOException {
        String queryPath = "dat/queryT-10.txt";
        String outputPath = "dat/results.txt";
        String stopWordPath = "dat/stopwords.txt";
        int topK = 10;
        int expand = 0;
        int nThreads = 1;
        Main.retrieveAll(queryPath, outputPath, stopWordPath, topK, expand, nThreads);
    }
}