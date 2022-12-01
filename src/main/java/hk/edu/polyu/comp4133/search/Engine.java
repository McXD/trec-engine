package hk.edu.polyu.comp4133.search;

import hk.edu.polyu.comp4133.index.*;
import hk.edu.polyu.comp4133.prep.Preprocessor;
import hk.edu.polyu.comp4133.utils.SearchUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The TREC search engine. An engine instance is created based on a pre-built index.
 * An engine instance should also have reference to the document collection corresponding to the index (i.e., the `file.txt`).
 */
public class Engine {
    private final InvertedFile index;
    private final Preprocessor preprocessor;
    private final DocumentMapper mapper;

    public Engine(InvertedFile index, Preprocessor preprocessor, DocumentMapper mapper) {
        this.index = index;
        this.preprocessor = preprocessor;
        this.mapper = mapper;
    }

    public enum QueryMode {
        VSM, PROXIMITY
    }
    public enum QueryExpansion {
        /**
         * Do not expand query
         */
        NONE,
        /**
         * Auto-detect if a term is negative or positive regarding the query
         */
        WEIGHTED,
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

    public List<TRECResult> search(TRECQuery query, int topK, QueryMode mode, QueryExpansion expansion, int maxDistance) {
        preprocessQuery(query);

        switch (mode) {
            case VSM:
                return searchVSM(query, topK, expansion);
            case PROXIMITY:
                return searchWithProximity(query, topK, maxDistance);
            default:
                throw new IllegalArgumentException("Unknown query mode: " + mode);
        }
    }

    public List<TRECResult> searchVSM(TRECQuery query, int topK, QueryExpansion expansion) {
        switch (expansion) {
            case NONE:
                return searchNonExpanded(query, topK);
            case WEIGHTED:
                return searchWeighted(query, topK);
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

        return getTrecResults(query, topK, scores);
    }

    /**
     * Used for short queries (queryT.txt).
     */
    public List<TRECResult> searchWithProximity(TRECQuery query, int topK, int maxDistance) {
        preprocessQuery(query);

        List<String> terms = Arrays.asList(query.text.split(" "));
        Map<Integer, Map<String, List<Integer>>> positions = new HashMap<>(); // docId -> term -> positions
        PostingList pl;
        for (String term : terms) {
            pl = index.getPostingList(term);
            for (Posting p : pl) {
                positions.putIfAbsent(p.docId, new HashMap<>());
                positions.get(p.docId).put(term, p.positions);
            }
        }

        List<TRECResult> results = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, List<Integer>>> entry : positions.entrySet()) {
            if (entry.getValue().size() < terms.size()) {  // doesn't contain all terms
                continue;
            }

            Map<String, List<Integer>> termPositions = entry.getValue().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(List::size)))  // sort by term frequency from low to high
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            List<List<Integer>> chains = new ArrayList<>();
            SearchUtils.searchProximity(chains, new ArrayList<>(), terms, termPositions, maxDistance);
            results.add(new TRECResult(query.id, entry.getKey(), mapper.map(entry.getKey()), 0, chains.size()));
        }
        results.sort(Comparator.comparingDouble(TRECResult::getScore).reversed());
        results.forEach(r -> r.ranking = results.indexOf(r));

        return results.subList(0, Math.min(topK, results.size()));
    }

    /**
     * Used for long queries (queryTDN.txt). Query should not be preprocessed.
     */
    public List<TRECResult> searchWeighted(TRECQuery query, int topK) {
        Map<Double, String> weighted = new HashMap<>(); // weight -> terms
        List<String> sentences = SearchUtils.splitParagraph(query.text);
        for (String s: sentences) {
            weighted.put(SearchUtils.getRelevancy(s), weighted.getOrDefault(SearchUtils.getRelevancy(s), "") + " " + s);
        }

        Map<Integer, Double> scores = new HashMap<>();
        Map<String, Double> wv;
        PostingList pl;
        double docCount = index.getDocCount();
        double docFreq;
        double idf;
        double tfQ;
        double tfD;
        double tfidfQ;
        double tfidfD;

        for (Map.Entry<Double, String> entry : weighted.entrySet()) {
            double weight = entry.getKey();
            String text = preprocessor.preprocess(entry.getValue());
            wv = textToWordVector(text);

            for (String term : wv.keySet()) {
                pl = index.getPostingList(term);
                docFreq = pl.getDocFreq();
                idf = Math.log(docCount / docFreq);
                tfQ = wv.get(term);
                tfidfQ = tfQ * idf;
                for (Posting p : pl) {
                    tfD = p.getTermFreq();
                    tfidfD = tfD * idf;
                    scores.put(p.docId, scores.getOrDefault(p.docId, 0.0) + weight * (tfidfQ * tfidfD));
                }
            }
        }

        return getTrecResults(query, topK, scores);
    }

    private List<TRECResult> getTrecResults(TRECQuery query, int topK, Map<Integer, Double> scores) {
        List<Integer> docIds = new ArrayList<>(scores.keySet());
        List<Double> lengths = index.getDocLengths(docIds);
        for (int i = 0; i < docIds.size(); i++) {
            scores.put(docIds.get(i), scores.get(docIds.get(i)) / lengths.get(i));
        }

        Map<Integer, Double> sorted = scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topK)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int rank = 0;
        String docName;
        List<TRECResult> ret = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sorted.entrySet()) {
            docName = mapper.map(entry.getKey());
            ret.add(new TRECResult(query.id, entry.getKey(), docName, rank, entry.getValue()));
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
