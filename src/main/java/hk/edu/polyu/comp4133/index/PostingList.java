package hk.edu.polyu.comp4133.index;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * A posting list is a list @see{Posting}s _ordered_ by the document id. It is also iterable (over the underlying postings).
 */
public class PostingList implements Iterable<Posting>, Serializable {
    private final LinkedList<Posting> postings;

    public PostingList() {
        postings = new LinkedList<>();
    }

    /**
     * Add a posting to the posting list
     */
    public void addPosting(Posting posting) {
        postings.add(posting);
    }

    public void addEntry(PostInputStream.PostEntry entry) {
        for (Posting posting : postings) {
            if (posting.docId == entry.docId) {
                posting.addPosition(entry.position);
                return;
            }
        }

        postings.add(new Posting(entry.docId, entry.position));
    }

    /**
     * Get the number of postings in the list
     */
    public int getDocFreq() {
        return postings.size();
    }

    public boolean hasDocId(int docId) {
        for (Posting posting : postings) {
            if (posting.docId == docId) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<Posting> iterator() {
        return postings.iterator();
    }

    @Override
    public String toString() {
        return "PostingList{" +
                "postings=" + postings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostingList postings1 = (PostingList) o;

        return postings1.postings.equals(this.postings);
    }

    @Override
    public int hashCode() {
        return postings.hashCode();
    }
}
