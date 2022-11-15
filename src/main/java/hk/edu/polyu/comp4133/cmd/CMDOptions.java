package hk.edu.polyu.comp4133.cmd;

import org.apache.commons.cli.Options;

public class CMDOptions {
    public static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("i", "index", true, "build index for the given postings file");
        options.addOption("b", "build-mode", true, "build mode: 0 for in-memory, 1 for sort-based, 2 for merged-based. default: 0");
        options.addOption("r", "retrieve", true, "retrieve documents for the given query file");
        options.addOption("s", "search", true, "search based on the given query");
        options.addOption("l", "load-mode", true, "how the index should be loaded: 0 for full, 1 for only dictionary. default: 0");
        options.addOption("k", "top-k", true, "top k documents to retrieve. default: 1000");
        options.addOption("e", "expansion", true, "query expansion: 0 for none, 1 for pseudo relevance feedback, 2 for local association analysis, 3 for local correlation analysis. default: 0");

        return options;
    }
}
