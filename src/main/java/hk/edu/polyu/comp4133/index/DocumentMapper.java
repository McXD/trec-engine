package hk.edu.polyu.comp4133.index;

public interface  DocumentMapper {
    /**
     * Maps a document id to document name
     */
    String map(int docId);
}
