package hk.edu.polyu.comp4133.index;

import java.io.*;

public class FilePostInputStream implements PostInputStream {
    private BufferedReader bf;

    /**
     * Construct a FilePostInputStream from a file.
     * @param filename the name of the file to read from
     */
    public FilePostInputStream(String filename) throws FileNotFoundException {
          bf = new BufferedReader(new FileReader(filename));
    }

    /**
     * Read the next posting from the file.
     * @return the next posting, or null if there is no more posting
     */
    public PostEntry next() {
        try {
            String line = bf.readLine();
            if (line == null) {
                return null;  // EOF
            }
            String[] parts = line.split(" ");
            PostEntry entry = new PostEntry();
            entry.term = parts[0];
            entry.docId = Integer.parseInt(parts[1]);
            entry.position = Integer.parseInt(parts[2]);
            return entry;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Close the file.
     */
    public void close() throws IOException {
        bf.close();
    }
}
