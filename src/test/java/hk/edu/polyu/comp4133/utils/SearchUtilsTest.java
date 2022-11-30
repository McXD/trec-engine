package hk.edu.polyu.comp4133.utils;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SearchUtilsTest {
    @Test
    void search() {
        int[] array = {3, 8, 11, 15, 18, 32, 40};
        int key = 1;
        int range = 2;
        int expected = 0;
        int actual = SearchUtils.binarySearchRanged(array, 0, array.length - 1, key, range);
        assertEquals(expected, actual);
    }

    @Test
    void isProximate() {
        int termPos = 25;
        int[] positions = {3, 8, 11, 15, 18, 32, 40};
        int range = 4;
        int offset = 1;
        boolean actual = SearchUtils.isProximate(termPos, positions, range, offset);
        System.out.println(actual);
    }

    @Test
    void searchProximity() {
        List<String> terms = Arrays.asList("information", "theoretic", "measure");
        Map<String, List<Integer>> positions = new LinkedHashMap<>();
        positions.put("information", Arrays.asList(1, 10, 31));
        positions.put("theoretic", Arrays.asList(3, 8, 11, 15, 18, 32, 40));
        positions.put("measure", Arrays.asList(4, 14, 12, 16));

        List<List<Integer>> results = new ArrayList<>();
        SearchUtils.searchProximity(results, new ArrayList<>(), terms, positions, 2);
        System.out.println(results);
    }
}