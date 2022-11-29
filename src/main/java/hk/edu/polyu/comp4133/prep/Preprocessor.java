package hk.edu.polyu.comp4133.prep;

public class Preprocessor {
    /**
     * Preprocess a query string. Carry out the following operations:
     * 1. Remove all stop words
     * 2. Stem all terms
     */

    public String preprocess(String term) {
        StopWordChecker stopWordChecker = new StopWordChecker();
        String [] query = term.split(" ");

        // Removing all the stop words
        String processedQuery = "";
        for (int i = 0; i < query.length; i++){
            if (!stopWordChecker.isStopWord(query[i])){
                processedQuery += query[i] + " ";
            }
        }

        // Stemming all terms
        query = processedQuery.split(" ");
        processedQuery = "";
        PorterStemmer porterStemmer = new PorterStemmer();
        for (int i = 0; i < query.length; i++){
            processedQuery += porterStemmer.stem(query[i]) + " ";
        }

        return processedQuery;
    }
}
