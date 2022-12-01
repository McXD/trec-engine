package hk.edu.polyu.comp4133.index;

import com.google.common.collect.Iterables;
import hk.edu.polyu.comp4133.utils.FileUtils;
import me.tongfei.progressbar.DelegatingProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static hk.edu.polyu.comp4133.utils.JedisUtils.buildPoolConfig;

public class RedisInvertedFile implements InvertedFile {
    JedisPool jedisPool;

    private final Logger logger = LoggerFactory.getLogger(RedisInvertedFile.class);

    public RedisInvertedFile(String host, int port) {
        jedisPool = new JedisPool(buildPoolConfig(), host, port);
        logger.info("Connected to Redis server at {}:{}", host, port);
    }

    public Void buildPart(long start, long end, File file) throws IOException {
        InputStream is = Files.newInputStream(file.toPath());
        is.skip(start);
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));

        long nRead = 0;
        long nTotal = end - start;
        int currentDocId = -1;
        Map<String, Posting> postingPerDoc = new HashMap<>();


        ProgressBar pb = new ProgressBarBuilder()
                .setTaskName(String.format("Building %8d", start))
                .setInitialMax(end - start)
                .setConsumer(new DelegatingProgressBarConsumer(logger::info))
                .setStyle(ProgressBarStyle.ASCII)
                .build();

        while (nRead < nTotal) {
            String line = bf.readLine();
            if (line == null) { // EOF
                break;
            }
            nRead += line.length() + 2;  // +2 for \r\n
            pb.stepBy(line.length() + 2);

            String[] parts = line.split(" ");
            String term = parts[0];
            int docId = Integer.parseInt(parts[1]);
            int position = Integer.parseInt(parts[2]);

            if (docId != currentDocId) {
                if (currentDocId != -1) { // not first doc, flush the current list
                    flush(currentDocId, postingPerDoc);
                }

                postingPerDoc = new HashMap<>();
                currentDocId = docId;
            }

            // add position to current posting list
            if (postingPerDoc.containsKey(term)) {
                postingPerDoc.get(term).addPosition(position);
            } else {
                postingPerDoc.put(term, new Posting(docId, position));
            }
        }

        // flush last posting list
        flush(currentDocId, postingPerDoc);

        return null;
    }

    private void flush(int docId, Map<String, Posting> postingPerDoc) {
        Jedis jedis = jedisPool.getResource();

        for (Map.Entry<String, Posting> entry : postingPerDoc.entrySet()) {
            // inverted index
            String key = "ind:" + entry.getKey();
            List<Integer> positions = entry.getValue().positions;
            String value = String.join(",", positions.stream().map(Object::toString).toArray(String[]::new));
            value = entry.getValue().docId + "," + value;
            jedis.rpush(key, value);

            // doc length
            key = "freq:" + entry.getValue().docId;
            jedis.hset(key, entry.getKey(), String.valueOf(positions.size()));
        }

        jedis.close();
    }

    public void calcDocLengthPart(double corpusSize, Set<String> keys) {
        ProgressBar pb = new ProgressBarBuilder()
                .setTaskName("Calculating doc length")
                .setInitialMax(keys.size())
                .setConsumer(new DelegatingProgressBarConsumer(logger::info))
                .setStyle(ProgressBarStyle.ASCII)
                .build();

        Jedis jedis = jedisPool.getResource();

        for (String key : keys) { // all docs
            Map<String, String> map = jedis.hgetAll(key);
            double sum = 0;
            double df;
            double tf;
            double idf;

            for (Map.Entry<String, String> entry : map.entrySet()) { // all terms in doc
                df = jedis.llen("ind:" + entry.getKey());
                idf = Math.log(corpusSize / df);
                tf = Double.parseDouble(entry.getValue());
                sum += Math.pow(tf * idf, 2);
            }
            jedis.set("len:" + key.substring(5), String.valueOf(Math.sqrt(sum)));
            jedis.del(key);

            pb.step();
        }

        jedis.close();
    }

    public Callable<Void> calcDocLengthPartTask(double corpusSize, Set<String> keys) {
        return () -> {
            calcDocLengthPart(corpusSize, keys);
            return null;
        };
    }

    public Callable<Void> buildPartTask(long start, long end, File file) {
        return () -> buildPart(start, end, file);
    }

    private void setCorpusSize(int size) {
        Jedis jedis = jedisPool.getResource();
        jedis.set("meta:corpusSize", String.valueOf(size));
        logger.info("Written corpus size: {}", size);
        jedis.close();
    }

    public void build(int nThreads, String postPath) throws IOException, InterruptedException {
        long[] positions = FileUtils.splitFileByDoc(postPath, nThreads);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            long start = i == 0 ? 0 : positions[i - 1];
            long end = positions[i];
            tasks.add(buildPartTask(start, end, new File(postPath)));
        }
        ExecutorService es = Executors.newFixedThreadPool(nThreads);
        es.invokeAll(tasks);

        logger.info("Finished building index. Starting to calculate document length.");

        tasks = new ArrayList<>();
        Set<String> keys = jedisPool.getResource().keys("freq:*");
        double corpusSize = keys.size();
        setCorpusSize((int) corpusSize);
        for (List<String> partition : Iterables.partition(keys, (int) corpusSize / nThreads)) {
            tasks.add(calcDocLengthPartTask(corpusSize, new HashSet<>(partition)));
        }
        es.invokeAll(tasks);

        es.shutdown();
    }

    @Override
    public PostingList getPostingList(String term) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> result = jedis.lrange("ind:" + term, 0, -1);

            PostingList postingList = new PostingList();
            for (String s : result) {
                postingList.addPosting(Posting.fromCompactString(s));
            }

            return postingList;
        }
    }

    @Override
    public double getDocLength(int docId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Double.parseDouble(jedis.get("len:" + docId));
        }
    }

    @Override
    public int getDocCount() {
        try (Jedis jedis = jedisPool.getResource()) {
            return Integer.parseInt(jedis.get("meta:corpusSize"));
        }
    }

    public List<Double> getDocLengths(List<Integer> ids) {
        int[] tmp = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            tmp[i] = ids.get(i);
        }

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mget(Arrays.stream(tmp).mapToObj(i -> "len:" + i).toArray(String[]::new))
                    .stream()
                    .map(Double::parseDouble).collect(Collectors.toList());
        }
    }
}
