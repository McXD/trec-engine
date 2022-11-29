package hk.edu.polyu.comp4133.prep;

import java.io.IOException;

public class Preprocessor {
    private final StopWordChecker stopWordChecker;
    private final Stemmer stemmer;
    public Preprocessor(String stopWordPath) throws IOException {
        stopWordChecker = new StopWordChecker(stopWordPath);
        stemmer = new PorterStemmer();
    }

    /**
     * Preprocess a query string. Carry out the following operations:
     * 1. Remove all stop words
     * 2. Stem all terms
     */
    public String preprocess(String text) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = text.split(" ");
        for (String token : tokens) {
            if (!stopWordChecker.isStopWord(token)) {
                sb.append(stemmer.stem(token)).append(" ");
            }
        }

        return sb.toString();
    }
}
