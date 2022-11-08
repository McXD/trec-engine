package hk.edu.polyu.comp4133;

import hk.edu.polyu.comp4133.cmd.CMDOptions;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        Options options = CMDOptions.getOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar trec.jar", options);
            } else if (cmd.hasOption("i")) {
                String postingsFile = cmd.getOptionValue("i");
                System.out.println("Indexing " + postingsFile);
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
        }
    }
}