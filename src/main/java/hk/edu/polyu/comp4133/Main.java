package hk.edu.polyu.comp4133;

import hk.edu.polyu.comp4133.cmd.CMDOptions;
import hk.edu.polyu.comp4133.index.FilePostInputStream;
import hk.edu.polyu.comp4133.index.InMemoryInvertedFile;
import hk.edu.polyu.comp4133.index.PostInputStream;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Options options = CMDOptions.getOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            BasicConfigurator.configure();
            Logger log = LoggerFactory.getLogger(Main.class);
            log.info("Starting program");

            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar trec.jar", options);
            } else if (cmd.hasOption("i")) {
                String input = cmd.getOptionValue("i");
                String inPath = input.split(":")[0];
                String outPath = input.split(":")[1];
                System.out.println("Indexing " + inPath + " to " + outPath);
                buildIndex(inPath, outPath);
            } else if (cmd.hasOption("r")) {
                String queryFile = cmd.getOptionValue("r");
                System.out.println("Retrieving " + queryFile);
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
    public static void buildIndex(String inPath, String outPath) throws IOException {
        PostInputStream s = new FilePostInputStream(inPath);
        InMemoryInvertedFile inv = InMemoryInvertedFile.build(s);
        inv.saveToDisk(outPath);
    }
}