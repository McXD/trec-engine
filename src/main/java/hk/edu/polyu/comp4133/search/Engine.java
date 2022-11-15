package hk.edu.polyu.comp4133.search;

import java.util.List;

/**
 * The TREC search engine. An engine instance is created based on a pre-built index.
 * An engine instance should also have reference to the document collection corresponding to the index (i.e., the `file.txt`).
 */
public class Engine {
    enum QueryExpansion {
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
        return null;
    }

    public List<TRECResult> search(List<TRECQuery> queries, int topK, QueryExpansion expansion) {
        return null;
    }
}
