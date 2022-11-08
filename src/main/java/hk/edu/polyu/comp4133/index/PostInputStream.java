package hk.edu.polyu.comp4133.index;

/**
 * A stream of postings. The stream can read directly from the 'post.txt' file or from a database.
 */
public interface PostInputStream extends AutoCloseable {
    class PostEntry {
        public String term;
        public int docId;
        public int position;
    }

    /**
     * Read the next posting from the stream.
     * @return the next posting, or null if there is no more posting.
     */
    PostEntry next();
}
