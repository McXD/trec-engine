package hk.edu.polyu.comp4133.index;

/**
 * A single posting.
 * Contains the document ID, term frequency.
 * Term positions in the document are not stored since phrase queries will not be supported.
 */
public class Posting {
    public int docId;
    public int termFreq;
    public int position;

    public Posting(int docId, int termFreq, int position) {
        this.docId = docId;
        this.termFreq = termFreq;
        this.position = position;
    }
}
