package hk.edu.polyu.comp4133.index;

import java.util.List;

public interface InvertedFile {
    PostingList getPostingList(String term);
    double getDocLength(int docId);
    List<Double> getDocLengths(List<Integer> docIds);
    int getDocCount();
}
