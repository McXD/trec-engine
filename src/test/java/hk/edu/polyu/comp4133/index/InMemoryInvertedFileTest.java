package hk.edu.polyu.comp4133.index;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

class InMemoryInvertedFileTest {
    @Test
    public void canIndex() throws FileNotFoundException {
        PostInputStream s = new FilePostInputStream("src/test/resources/post.txt");
        InMemoryInvertedFile invertedFile = InMemoryInvertedFile.build(s);
        System.out.println(invertedFile.getPostingList("peaC"));
        System.out.println(invertedFile.getDocLength(0));
    }

    @Test
    public void canPersist() throws IOException, ClassNotFoundException {
        PostInputStream s = new FilePostInputStream("src/test/resources/post.txt");
        InMemoryInvertedFile inv = InMemoryInvertedFile.build(s);
        inv.saveToDisk("src/test/resources/invertedFile.dat");
        InMemoryInvertedFile loaded = InMemoryInvertedFile.loadFromDisk("src/test/resources/invertedFile.dat");

        Assertions.assertEquals(inv.getPostingList("peaC"), loaded.getPostingList("peaC"));
        Assertions.assertEquals(inv.getDocLength(0), loaded.getDocLength(0));
    }
}