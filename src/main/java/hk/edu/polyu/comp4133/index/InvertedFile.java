package hk.edu.polyu.comp4133.index;

public interface InvertedFile {
    PostingList getPostingList(String term);
    double getDocLength(int docId);
    int getDocCount();
}
