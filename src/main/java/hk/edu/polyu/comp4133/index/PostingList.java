
package hk.edu.polyu.comp4133.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A posting list is a list @see{Posting}s _ordered_ by the document id. It is also iterable (over the underlying postings).
 */
public class PostingList implements Iterable<Posting> {
    /**
     * Add a posting to the posting list
     */
    List<Posting> postingList = new ArrayList<Posting>();
    public void addPosting(Posting posting) {
        // TODO
        this.postingList.add(posting);
    }

    /**
     * Get the number of postings in the list
     */
    public int getDocFreq() {
        // may be need to modify?
        return this.postingList.size();
    }

    @Override
    public Iterator<Posting> iterator() {
        return null;
    }
}
