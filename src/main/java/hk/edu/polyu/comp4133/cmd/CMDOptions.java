package hk.edu.polyu.comp4133.cmd;

import org.apache.commons.cli.Options;

public class CMDOptions {
    public static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("i", "index", true, "build index for the given postings file");
        options.addOption("r", "retrieve", true, "retrieve documents for the given query file");
        options.addOption("s", "search", true, "search based on the given query");

        return options;
    }
}
