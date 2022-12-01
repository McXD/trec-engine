package hk.edu.polyu.comp4133.utils;

import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtils {
    public static void searchProximity(List<List<Integer>> results, List<Integer> result, List<String> terms, Map<String, List<Integer>> positions, int range) {
        if (result.size() != 0) {
            if (result.size() == terms.size()) {
                results.add(result);
                return;
            }

            String currentTerm = terms.get(result.size());
            String prevTerm = terms.get(result.size() - 1);
            int prevPos = result.get(result.size() - 1);

            int[] currentPositions = SearchUtils.toArray(positions.get(currentTerm));
            int pos2Ind = SearchUtils.binarySearchRanged(
                    currentPositions,
                    0,
                    currentPositions.length - 1,
                    prevPos - terms.indexOf(prevTerm) + terms.indexOf(currentTerm), // plus offset
                    range);

            if (pos2Ind != -1) {
                result.add(currentPositions[pos2Ind]);
                if (terms.indexOf(currentTerm) == terms.size() - 1) { // last
                    results.add(result);
                } else {
                    searchProximity(results, result, terms, positions, range);
                }
            }
        } else {
            for (int pos : positions.get(terms.get(0))) {
                result = new ArrayList<>();
                result.add(pos);
                searchProximity(results, result, terms, positions, range);
            }
        }
    }

    public static boolean isProximate(int termPos, int[] positions, int range, int offset) {
        return SearchUtils.binarySearchRanged(positions, 0, positions.length - 1, termPos, range + offset) != -1;
    }

    // find the first index of value within the range of the given term in the given array
    public static int binarySearchRanged(int[] array, int top, int bottom, int key, int range) {
        if (top > bottom) {
            return -1;
        }

        int mid = (top + bottom) / 2;
        if (isInRange(array, mid, key, range)) {
            return mid;
        } else if (array[mid] < key) {
            return binarySearchRanged(array, mid + 1, bottom, key, range);
        } else {
            return binarySearchRanged(array, top, mid - 1, key, range);
        }
    }

    public static boolean isInRange(int[] array, int mid, int key, int range) {
        return array[mid] >= key - range && array[mid] <= key + range;
    }

    public static int[] toArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static List<String> splitParagraph(String p) {
        List<String> ret = new ArrayList<>();

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(p);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            ret.add(p.substring(start, end));
        }

        return ret;
    }

    static Map<String, Double> weights;

    static {
        weights = new HashMap<>();
        weights.put("relevant", 1.5);
        weights.put("irrelevant", -1.0);
        weights.put("not relevant", -1.0);
    }
    public static double getRelevancy(String s) {
        if (s.contains("not relevant")) {
            return -1;
        } else if (s.contains("irrelevant")) {
            return -1;
        } else if (s.contains("relevant")) {
            return 2.0;
        } else {
            return 2.0;
        }
    }
}
