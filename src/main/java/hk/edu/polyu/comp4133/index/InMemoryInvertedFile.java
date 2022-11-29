package hk.edu.polyu.comp4133.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * InMemoryInvertedFile is an implementation of InvertedFile that stores the entire inverted file structure in memory.
 */
public class InMemoryInvertedFile implements Serializable {
    protected final Map<String, PostingList> index;
    protected final Map<Integer, Double> docLengths;

    private static final Logger logger = LoggerFactory.getLogger(InMemoryInvertedFile.class);

    protected InMemoryInvertedFile() {
        index = new HashMap<>();
        docLengths = new HashMap<>();
    }

    /**
     * Build the index.
     */
    public static InMemoryInvertedFile build(PostInputStream is) {
        InMemoryInvertedFile invertedFile = new InMemoryInvertedFile();
        Map<String, PostingList> index = invertedFile.index;
        Map<Integer, Double> docLengths = invertedFile.docLengths;


        Map<Integer, Map<String, Double>> termFreqs = new HashMap<>(); // term frequency for each document
        Map<String, Double> termFreq;  // term frequency for a document
        int corpusSize = 0;
        int docId = -1;
        PostInputStream.PostEntry entry;

        while ((entry = is.next()) != null) {
            if (entry.docId != docId) { // new document
                logger.info("Processing document {}", entry.docId);
                termFreqs.put(entry.docId, new HashMap<>());
                corpusSize++;
            }

            // update index
            if (index.containsKey(entry.term)) {
                index.get(entry.term).addEntry(entry);
            } else {
                PostingList postingList = new PostingList();
                postingList.addEntry(entry);
                index.put(entry.term, postingList);
            }

            // update term frequency for the document
            termFreq = termFreqs.get(entry.docId);
            if (termFreq.containsKey(entry.term)) {
                termFreq.put(entry.term, termFreq.get(entry.term) + 1);
            } else {
                termFreq.put(entry.term, 1.0);
            }

            docId = entry.docId;
        }

        // calculate document TF-IDF length
        // ref: wikipedia page
        for (Map.Entry<Integer, Map<String, Double>> docEntry : termFreqs.entrySet()) {
            docId = docEntry.getKey();
            termFreq = docEntry.getValue();

            // normalize term frequency
            double totalFreq = termFreq.values().stream().reduce(0.0, Double::sum);
            for (Map.Entry<String, Double> termEntry : termFreq.entrySet()) {
                termEntry.setValue(termEntry.getValue() / totalFreq);
            }

            // times IDF
            for (Map.Entry<String, Double> termEntry : termFreq.entrySet()) {
                double idf = Math.log((double) corpusSize / index.get(termEntry.getKey()).getDocFreq());
                termEntry.setValue(termEntry.getValue() * idf);
            }

            // calculate length
            double length = Math.sqrt(termFreq.values().stream().map(x -> x * x).reduce(0.0, Double::sum));
            docLengths.put(docId, length);
        }

        return invertedFile;
    }

    public static InMemoryInvertedFile loadFromDisk(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(path)));
        return (InMemoryInvertedFile) ois.readObject();
    }

    public void saveToDisk(String path) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));
        oos.writeObject(this);
        oos.close();
    }

    /**
     * Get the posting list of a term
     */
    public PostingList getPostingList(String term) {
        return index.get(term);
    }

    /**
     * Get vector (e.g., TF-IDF) length of the document.
     * This value should be pre-computed and stored in the index.
     */
    public double getDocLength(int docId) {
        return docLengths.get(docId);
    }
}
