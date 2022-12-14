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
                String inPath = cmd.getOptionValue("i", "./dat/post.txt");
                buildIndex(inPath, getNThreads(cmd));
            }  else if (cmd.hasOption("d")) {
                String input = cmd.getOptionValue("d", "./dat/file.txt");
                buildDocMap(input, getNThreads(cmd));
            } else if (cmd.hasOption("r")) {
                String queryPath = cmd.getOptionValue("r", "./dat/queryT.txt");
                String outputPath = cmd.getOptionValue("o", "result.txt");
                String stopWordPath = cmd.getOptionValue("p", "./dat/stopwords.txt");
                int mode = Integer.parseInt(cmd.getOptionValue("m", "0"));
                int topK = Integer.parseInt(cmd.getOptionValue("k", "1000"));
                int expand = Integer.parseInt(cmd.getOptionValue("e", "0"));
                int nThreads = getNThreads(cmd);
                int maxDistance = Integer.parseInt(cmd.getOptionValue("x", "10"));

                retrieveAll(queryPath, outputPath, stopWordPath, topK, mode, expand, maxDistance, nThreads);
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

    public static void buildIndex(String inPath, int nThreads) throws IOException, InterruptedException {
        RedisInvertedFile inv = new RedisInvertedFile(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")));
        inv.build(nThreads, inPath);
    }

    public static void buildDocMap(String inPath, int nThreads) throws IOException, InterruptedException {
        RedisDocumentMapper docMap = new RedisDocumentMapper(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")));
        docMap.index(inPath, nThreads);
    }

    public static void retrieveAll(String queryFilePath, String outputPath, String stopWordPath, int topK, int mode, int expand, int maxDistance, int nThreads) throws IOException {
        logger.info("Retrieving from {} to {} with topK = {}, expand = {}, nThreads = {}", queryFilePath, outputPath, topK, expand, nThreads);

        Engine.QueryExpansion expansion = Engine.QueryExpansion.values()[expand];
        Engine.QueryMode modeEnum = Engine.QueryMode.values()[mode];
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
            futures.add(es.submit(searchTask(e, query, topK, modeEnum, expansion, maxDistance, pb)));
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

    static Callable<List<TRECResult>> searchTask(Engine e, TRECQuery query, int topK, Engine.QueryMode mode, Engine.QueryExpansion expansion, int maxDistance, ProgressBar pb) {
        return () -> {
            List<TRECResult> results = e.search(query, topK, mode, expansion, maxDistance);
            pb.step();
            return results;
        };
    }

    static int getNThreads(CommandLine cmd) {
        return Integer.parseInt(cmd.getOptionValue("t", "10"));
    }

    static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("i", "index", true, "build index for the given postings file. default: ./dat/post.txt");
        options.addOption("r", "retrieve", true, "retrieve documents for the given query file. default: ./dat/queryT.txt");
        options.addOption("k", "top-k", true, "top k documents to retrieve. default: 1000");
        options.addOption("e", "expansion", true, "query expansion method for VSM mode: 0 for none, 1 for weighted, 2 for pseudo relevance feedback, 3 for local association analysis, 4 for local correlation analysis. default: 0");
        options.addOption("t", "threads", true, "number of threads to use. default: 10");
        options.addOption("o", "output", true, "output file path. default: output.txt");
        options.addOption("p", "stopwords", true, "stopwords file path. default: ./dat/stopwords.txt");
        options.addOption("d", "docmap", true, "build document map. default: ./dat/file.txt");
        options.addOption("m", "mode", true, "query mode: 0 for VSM, 1 for Proximity. default: 0");
        options.addOption("x", "max-distance", true, "maximum distance between two terms in a phrase query. default: 10");

        return options;
    }
}