package hk.edu.polyu.comp4133.search;

/**
 * A TREC result.
 */
public class TRECResult {
    public int queryId;
    public int docId;
    public int ranking;
    public double score;

    public TRECResult(int queryId, int docId, int ranking, double score) {
        this.queryId = queryId;
        this.docId = docId;
        this.ranking = ranking;
        this.score = score;
    }

    @Override
    public String toString() {
        return "TRECResult{" +
                "queryId=" + queryId +
                ", docId=" + docId +
                ", ranking=" + ranking +
                ", score=" + score +
                '}';
    }

    public String toTRECString(String runId) {
        return String.format("%d Q0 %d %d %.2f %s", queryId, docId, ranking, score, runId);
    }
}
