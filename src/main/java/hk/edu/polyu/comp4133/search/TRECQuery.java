package hk.edu.polyu.comp4133.search;

/**
 * A TREC query.
 */
public class TRECQuery {
    public int id;
    public String text;

    public TRECQuery(int id, String text) {
        this.id = id;
        this.text = text;
    }
}
