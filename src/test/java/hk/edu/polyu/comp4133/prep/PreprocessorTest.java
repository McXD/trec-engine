package hk.edu.polyu.comp4133.prep;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PreprocessorTest {
    @Test
    void preprocess() throws IOException {
        BufferedReader queries = new BufferedReader(new FileReader("dat/queryT.txt"));
        Preprocessor pp = new Preprocessor("dat/stopwords.txt");

        for (int i = 0; i < 10; i++) {
            String line = queries.readLine();
            System.out.println(line);
            System.out.println(pp.preprocess(line));
        }
    }
}
