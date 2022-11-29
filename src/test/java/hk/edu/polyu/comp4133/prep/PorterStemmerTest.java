package hk.edu.polyu.comp4133.prep;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class PorterStemmerTest {
    @Test
    void stem() {
        PorterStemmer stemmer = new PorterStemmer();

        assertEquals("orgaN", stemmer.stem("organs"));
    }
}