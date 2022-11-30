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
        int nThreads = 5;
        Main.retrieveAll(queryPath, outputPath, stopWordPath, topK, 0, expand, 0, nThreads);
    }

    @Test
    void retrieveAllWithProximity() throws IOException {
        String queryPath = "dat/queryT.txt";
        String outputPath = "dat/test-results.txt";
        String stopWordPath = "dat/stopwords.txt";
        int topK = 1000;
        int expand = 0;
        int nThreads = 10;

        Main.retrieveAll(queryPath, outputPath, stopWordPath, topK, 1, expand, 100, nThreads);
    }
}