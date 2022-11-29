package hk.edu.polyu.comp4133.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A single posting.
 * Contains the document ID, term frequency.
 */
public class Posting implements Serializable {
    public int docId;
    public List<Integer> positions;

    public Posting(int docId, int position) {
        this.docId = docId;
        this.positions = new ArrayList<>(); // auto-resize
        this.positions.add(position);
    }

    public void addPosition(int position) {
        positions.add(position);
    }

    public int getTermFreq() {
        return positions.size();
    }

    @Override
    public String toString() {
        return "Posting{" +
                "docId=" + docId +
                ", positions=" + positions +
                '}';
    }

    public String toCompactString() {
        return String.join(",", positions.stream().map(Object::toString).toArray(String[]::new));
    }

    public static Posting fromCompactString(String s) {
        String[] parts = s.split(",");
        int docId = Integer.parseInt(parts[0]);
        Posting posting = new Posting(docId, Integer.parseInt(parts[1]));
        for (int i = 2; i < parts.length; i++) {
            posting.addPosition(Integer.parseInt(parts[i]));
        }
        return posting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        if (docId != posting.docId) return false;
        return this.positions.equals(posting.positions);
    }

    @Override
    public int hashCode() {
        int result = docId;
        result = 31 * result + (positions != null ? positions.hashCode() : 0);
        return result;
    }
}
