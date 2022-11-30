package hk.edu.polyu.comp4133.index;

import hk.edu.polyu.comp4133.utils.FileUtils;
import me.tongfei.progressbar.DelegatingProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RedisDocumentMapper implements DocumentMapper {
    JedisPool jedisPool;

    public RedisDocumentMapper(String host, int port) {
        jedisPool = new JedisPool(host, port);
    }

    public void index(String filePath, int nThreads) throws IOException, InterruptedException {
        File file = new File(filePath);
        long total = file.length();
        ProgressBar pb = new ProgressBarBuilder()
                .setTaskName("Indexing DocName")
                .setInitialMax(total)
                .setStyle(ProgressBarStyle.ASCII)
                .build();

        long[] positions = FileUtils.splitFileByDoc(filePath, nThreads);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            long start = i == 0 ? 0 : positions[i - 1];
            long end = positions[i];
            tasks.add(indexPartTask(start, end, file, pb));
        }
        ExecutorService es = Executors.newFixedThreadPool(nThreads);
        es.invokeAll(tasks);

        es.shutdown();
    }

    private void indexPart(long start, long end, File file, ProgressBar pb) throws IOException {
        Jedis jedis = jedisPool.getResource();
        InputStream is = Files.newInputStream(file.toPath());
        is.skip(start);
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        long nRead = 0;
        long nTotal = end - start;
        String line;
        String docId;
        String docName;

        while ((line = bf.readLine()) != null) {
            nRead += line.length() + 2;  // +2 for \r
            if (nRead > nTotal) {
                break;
            }

            String[] parts = line.split(" ");
            docId = parts[0];
            docName = parts[3];
            jedis.set("name:" + docId, docName);

            pb.stepBy(line.length() + 2);
        }

        jedis.close();
    }

    private Callable<Void> indexPartTask(long start, long end, File file, ProgressBar pb) {
        return () -> {
            indexPart(start, end, file, pb);
            return null;
        };
    }

    @Override
    public String map(int docId) {
        // TODO: repeated open and close connection is costly
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("name:" + docId);
        }
    }
}
