package hk.edu.polyu.comp4133.search;

/**
 * A TREC result.
 */
public class TRECResult {
    public int queryId;
    public int docId;
    public String docName;
    public int ranking;
    public double score;

    public TRECResult(int queryId, int docId, String docName, int ranking, double score) {
        this.queryId = queryId;
        this.docId = docId;
        this.docName = docName;
        this.ranking = ranking;
        this.score = score;
    }

    @Override
    public String toString() {
        return "TRECResult{" +
                "queryId=" + queryId +
                ", docId=" + docId +
                ", docName='" + docName + '\'' +
                ", ranking=" + ranking +
                ", score=" + score +
                '}';
    }

    public String toTRECString(String runId) {
        return String.format("%d Q0 %s %d %.2f %s", queryId, docName, ranking, score, runId);
    }
}
