package hk.edu.polyu.comp4133.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
