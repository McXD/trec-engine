package hk.edu.polyu.comp4133;

import hk.edu.polyu.comp4133.index.*;
import hk.edu.polyu.comp4133.prep.Preprocessor;
import hk.edu.polyu.comp4133.search.Engine;
import hk.edu.polyu.comp4133.search.TRECQuery;
import hk.edu.polyu.comp4133.search.TRECResult;
import me.tongfei.progressbar.DelegatingProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar trec.jar", options);
            } else if (cmd.hasOption("i")) {
                String input = cmd.getOptionValue("i");
                String inPath = input.split(";")[0];
                String redisUrl = input.split(";")[1];
                System.out.println("Indexing " + inPath + " to " + redisUrl);
                buildIndex(inPath, redisUrl);
            }  else if (cmd.hasOption("d")) {
                String input = cmd.getOptionValue("d");
                int nThreads = Integer.parseInt(cmd.getOptionValue("t", "1"));
                buildDocMap(input, nThreads);
            } else if (cmd.hasOption("r")) {
                String queryPath = cmd.getOptionValue("r");
                String outputPath = cmd.getOptionValue("o");
                String stopWordPath = cmd.getOptionValue("p");
                int topK = Integer.parseInt(cmd.getOptionValue("k"));
                int expand = Integer.parseInt(cmd.getOptionValue("e"));
                int nThreads = Integer.parseInt(cmd.getOptionValue("t"));

                retrieveAll(queryPath, outputPath, stopWordPath, topK, expand, nThreads);
            } else if (cmd.hasOption("s")) {
                String query = cmd.getOptionValue("s");
                System.out.println("Searching " + query);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar trec.jar", options);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void buildIndex(String inPath, String redisUrl) throws IOException, InterruptedException {
        String host = redisUrl.split(":")[0];
        int port = Integer.parseInt(redisUrl.split(":")[1]);

        RedisInvertedFile inv = new RedisInvertedFile(host, port);
        inv.build(10, inPath);
    }

    public static void buildDocMap(String inPath, int nThreads) throws IOException, InterruptedException {
        RedisDocumentMapper docMap = new RedisDocumentMapper(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")));
        docMap.index(inPath, nThreads);
    }

    public static void retrieveAll(String queryFilePath, String outputPath, String stopWordPath, int topK, int expand, int nThreads) throws IOException {
        logger.info("Retrieving from {} to {} with topK = {}, expand = {}, nThreads = {}", queryFilePath, outputPath, topK, expand, nThreads);

        Engine.QueryExpansion expansion = Engine.QueryExpansion.values()[expand];
        BufferedReader queries = new BufferedReader(new FileReader(queryFilePath));
        long total = Files.lines(new File(queryFilePath).toPath()).count();
        BufferedWriter output = new BufferedWriter(new FileWriter(outputPath));
        InvertedFile inv = new RedisInvertedFile(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")));
        RedisDocumentMapper docMap = new RedisDocumentMapper(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")));
        Preprocessor preprocessor = new Preprocessor(stopWordPath);
        Engine e = new Engine(inv, preprocessor, docMap);
        ExecutorService es = Executors.newFixedThreadPool(nThreads);
        ProgressBar pb = new ProgressBarBuilder()
                .setTaskName("Retrieving")
                .setInitialMax(total)
                .setConsumer(new DelegatingProgressBarConsumer(logger::info))
                .setStyle(ProgressBarStyle.ASCII)
                .build();

        String line;
        int queryId;
        String queryText;
        TRECQuery query;
        List<Future<List<TRECResult>>> futures = new ArrayList<>();
        List<TRECResult> results;
        while ((line = queries.readLine()) != null) {
            queryId = Integer.parseInt(line.split(" ")[0]);
            queryText = line.substring(line.indexOf(" ") + 1);
            query = new TRECQuery(queryId, queryText);
            futures.add(es.submit(searchTask(e, query, topK, expansion, pb)));
        }

        // futures are ordered by queryId (as we submit them in order)
        for (Future<List<TRECResult>> future : futures) {
            try {
                results = future.get();
                for (TRECResult result : results) {
                    output.write(result.toTRECString("COMP4133"));
                    output.newLine();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        es.shutdown();
        output.flush();
        queries.close();
        output.close();
    }

    static Callable<List<TRECResult>> searchTask(Engine e, TRECQuery query, int topK, Engine.QueryExpansion expansion, ProgressBar pb) {
        return () -> {
            List<TRECResult> results = e.search(query, topK, expansion);
            pb.step();
            return results;
        };
    }

    static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("i", "index", true, "build index for the given postings file");
        options.addOption("r", "retrieve", true, "retrieve documents for the given query file");
        options.addOption("s", "search", true, "search based on the given query");
        options.addOption("k", "top-k", true, "top k documents to retrieve. default: 1000");
        options.addOption("e", "expansion", true, "query expansion: 0 for none, 1 for pseudo relevance feedback, 2 for local association analysis, 3 for local correlation analysis. default: 0");
        options.addOption("t", "threads", true, "number of threads to use. default: 1");
        options.addOption("o", "output", true, "output file path. default: output.txt");
        options.addOption("p", "stopwords", true, "stopwords file path. default: stopwords.txt");
        options.addOption("d", "docmap", true, "document mapping file path. default: file.txt");

        return options;
    }
}