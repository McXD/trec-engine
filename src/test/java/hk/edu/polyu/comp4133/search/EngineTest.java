package hk.edu.polyu.comp4133.search;

import hk.edu.polyu.comp4133.index.RedisDocumentMapper;
import hk.edu.polyu.comp4133.index.RedisInvertedFile;
import hk.edu.polyu.comp4133.prep.Preprocessor;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class EngineTest {
    @Test
    void plainSearch() throws IOException {
        Engine e = new Engine(
                new RedisInvertedFile("localhost", 6379),
                new Preprocessor("dat/stopwords.txt"),
                new RedisDocumentMapper("localhost", 6379)
        );

        BufferedReader queries = new BufferedReader(new FileReader("dat/queryTDN.txt"));

        for (int i = 0; i < 10; i++) {
            String line = queries.readLine();
            int queryId = Integer.parseInt(line.split(" ")[0]);
            String queryText = line.substring(line.indexOf(" ") + 1);

            System.out.println(line);
            System.out.println(e.search(new TRECQuery(queryId, queryText), 10, Engine.QueryMode.VSM, Engine.QueryExpansion.NONE, 0));
        }
    }

    @Test
    void proximitySearch() throws IOException {
        Engine e = new Engine(
                new RedisInvertedFile("localhost", 6379),
                new Preprocessor("dat/stopwords.txt"),
                new RedisDocumentMapper("localhost", 6379)
        );

        BufferedReader queries = new BufferedReader(new FileReader("dat/queryT.txt"));

        for (int i = 0; i < 10; i++) {
            String line = queries.readLine();
            int queryId = Integer.parseInt(line.split(" ")[0]);
            String queryText = line.substring(line.indexOf(" ") + 1);

            System.out.println(line);
            System.out.println(e.searchWithProximity(new TRECQuery(queryId, queryText), 50, 2));
        }
    }

    @Test
    void searchWeighted() throws IOException {
        Engine e = new Engine(
                new RedisInvertedFile("localhost", 6379),
                new Preprocessor("dat/stopwords.txt"),
                new RedisDocumentMapper("localhost", 6379)
        );

        BufferedReader queries = new BufferedReader(new FileReader("dat/queryTDN.txt"));

        for (int i = 0; i < 10; i++) {
            String line = queries.readLine();
            int queryId = Integer.parseInt(line.split(" ")[0]);
            String queryText = line.substring(line.indexOf(" ") + 1);

            System.out.println(line);
            System.out.println(e.searchWeighted(new TRECQuery(queryId, queryText), 10));
        }
    }
}