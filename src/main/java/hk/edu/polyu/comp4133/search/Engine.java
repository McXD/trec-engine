package hk.edu.polyu.comp4133.search;

import hk.edu.polyu.comp4133.index.InvertedFile;
import hk.edu.polyu.comp4133.index.Posting;
import hk.edu.polyu.comp4133.index.PostingList;
import hk.edu.polyu.comp4133.prep.Preprocessor;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The TREC search engine. An engine instance is created based on a pre-built index.
 * An engine instance should also have reference to the document collection corresponding to the index (i.e., the `file.txt`).
 */
public class Engine {
    private Logger logger = Logger.getLogger(Engine.class.getName());
    private final InvertedFile index;
    private final Preprocessor preprocessor;

    public Engine(InvertedFile index, Preprocessor preprocessor) {
        this.index = index;
        this.preprocessor = preprocessor;
    }

    public enum QueryExpansion {
        /**
         * Do not expand query
         */
        NONE,
        /**
         * Expand query using feedback with the top 10 documents in the result set
         */
        PSEUDO_RELEVANCE_FEEDBACK,
        /**
         * Expand query using a local term _association_ matrix (normalized) from the top 10 documents in the result set (top 3 for each query term)
         */
        LOCAL_ASSOCIATION_ANALYSIS,
        /**
         * Expand query using a local term _metric correlation_ matrix (normalized) from the top 10 documents in the result set (top 3 for each query term)
         */
        LOCAL_CORRELATION_ANALYSIS
    }

    public List<TRECResult> search(TRECQuery query, int topK, QueryExpansion expansion) {
        preprocessQuery(query);

        switch (expansion) {
            case NONE:
                return searchNonExpanded(query, topK);
            case PSEUDO_RELEVANCE_FEEDBACK:
//                return searchWithPseudoRelevanceFeedback(query, topK);
            case LOCAL_ASSOCIATION_ANALYSIS:
//                return searchWithLocalAssociationAnalysis(query, topK);
            case LOCAL_CORRELATION_ANALYSIS:
//                return searchWithLocalCorrelationAnalysis(query, topK);
            default:
                throw new IllegalArgumentException("Unknown query expansion method");
        }
    }

    List<TRECResult> searchNonExpanded(TRECQuery query, int topK) {
        Map<String, Double> wv = textToWordVector(query.text);
        Map<Integer, Double> scores = new HashMap<>();
        PostingList pl;
        double docCount = index.getDocCount();
        double docFreq;
        double idf;
        double tfQ;
        double tfD;
        double tfidfQ;
        double tfidfD;

        for (String term : wv.keySet()) {
            pl = index.getPostingList(term);
            docFreq = pl.getDocFreq();
            idf = Math.log(docCount / docFreq);
            tfQ = wv.get(term);
            tfidfQ = tfQ * idf;
            for (Posting p : pl) {
                tfD = p.getTermFreq();
                tfidfD = tfD * idf;
                scores.put(p.docId, scores.getOrDefault(p.docId, 0.0) + tfidfQ * tfidfD);
            }
        }

        scores.replaceAll((i, v) -> scores.get(i) / index.getDocLength(i));

        Map<Integer, Double> sorted = scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topK)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int rank = 0;
        List<TRECResult> ret = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sorted.entrySet()) {
            ret.add(new TRECResult(query.id, entry.getKey(), rank, entry.getValue()));
            rank++;
        }

        return ret;
    }

    private Map<String, Double> textToWordVector(String text) {
        String[] tokens = text.split(" ");
        Map<String, Double> wordVector = new HashMap<>();
        for (String token : tokens) {
            if (wordVector.containsKey(token)) {
                wordVector.put(token, wordVector.get(token) + 1);
            } else {
                wordVector.put(token, 1.0);
            }
        }
        return wordVector;
    }

    private void preprocessQuery(TRECQuery query) {
        query.text = preprocessor.preprocess(query.text);
    }
}
