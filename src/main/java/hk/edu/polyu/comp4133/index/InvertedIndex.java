package hk.edu.polyu.comp4133.index;

/**
 * The inverted index. An index instance is built on a pre-processed corpus.
 * The built index should be persisted, either in a binary file or the database.
 * The index has document length data built-in.
 */
public class InvertedIndex {
    /**
     * Build the index.
     */
    public void build(PostInputStream is) {
        // TODO
    }

    /**
     * Get the posting list of a term
     */
    public PostingList getPostingList(String term) {
        // TODO
        return null;
    }

    /**
     * Get vector (e.g., TF-IDF) length of the document.
     * This value should be pre-computed and stored in the index.
     */
    public double getDocLength(int docId) {
        // TODO
        return 0;
    }
}
