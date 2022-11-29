package hk.edu.polyu.comp4133.prep;

import javax.swing.*;
import java.io.*;
import java.util.Set;
import java.util.TreeSet;

public class StopWordChecker {
    Set<String> stopWords = new TreeSet<>();

    public StopWordChecker(String path) throws IOException {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String stopWord;

        //assign stop words to tree set

        while ((stopWord = br.readLine()) != null) {
            stopWords.add(stopWord);
        }

        br.close();
    }

    /**
     * Check if a term is a stop word.
     *
     * @param term the term to check
     * @return true if the term is a stop word, false otherwise
     */
    public boolean isStopWord(String term) {
        return stopWords.contains(term);
    }
}
