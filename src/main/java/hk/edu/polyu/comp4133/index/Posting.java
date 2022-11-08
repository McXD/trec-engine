package hk.edu.polyu.comp4133.index;

/**
 * A single posting.
 * Contains the document ID, term frequency and positions of the term in the document.
 * Reference: <a href="https://nlp.stanford.edu/IR-book/html/htmledition/queries-as-vectors-1.html">IR Book</a>
 */
public class Posting {
    private final int docId;
    private final int termFreq;
    private final int[] positions;

    public Posting(int docId, int termFreq, int[] positions) {
        this.docId = docId;
        this.termFreq = termFreq;
        this.positions = positions;
    }

    public int getDocId() {
        return docId;
    }

    public int getTermFreq() {
        return termFreq;
    }

    public int[] getPositions() {
        return positions;
    }
}
