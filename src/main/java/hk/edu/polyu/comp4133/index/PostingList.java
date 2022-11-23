package hk.edu.polyu.comp4133.index;

import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

/**
 * A posting list is a list @see{Posting}s _ordered_ by the document id. It is also iterable (over the underlying postings).
 */
public class PostingList implements Iterable<Posting> {
    /**
     * Add a posting to the posting list
     */
    public void addPosting(Posting posting) {
        // TODO
        HashMap<String, List<Posting>> block = new HashMap<String, int<docId>>();
    }

    /**
     * Get the number of postings in the list
     */
    public int getDocFreq() {
        // TODO
        return block.get(docId);
    }

    @Override
    public Iterator<Posting> iterator() {
        return null;
    }
}
