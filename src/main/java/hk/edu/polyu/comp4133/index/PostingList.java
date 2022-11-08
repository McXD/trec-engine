package hk.edu.polyu.comp4133.index;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A posting list is a list @see{Posting}s _ordered_ by the document id. It is also iterable (over the underlying postings).
 */
public class PostingList implements Iterable<Posting> {
    /**
     * Add a posting to the posting list
     */
    public void addPosting(Posting posting) {
        // TODO
    }

    /**
     * Get the number of postings in the list
     */
    public int getDocFreq() {
        // TODO
        return 0;
    }

    @Override
    public Iterator<Posting> iterator() {
        return null;
    }
}
