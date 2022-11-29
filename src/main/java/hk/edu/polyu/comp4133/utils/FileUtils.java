package hk.edu.polyu.comp4133.utils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtils {
    // returns offsets of starting positions that roughly divide the file into nThreads parts
    public static long[] splitFile(String path, int nParts) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path, "r");
        long fSize = file.length();
        long tSize = fSize / nParts;
        long[] offsets = new long[nParts];
        offsets[nParts - 1] = fSize;

        for (int i = 0; i < nParts - 1; i++) {
            file.seek(tSize * (i + 1));
            file.readLine();  // skip the current line since it may be incomplete

            // split at new document
            String line;
            line = file.readLine();
            int docId = Integer.parseInt(line.split(" ")[1]);
            while ((line = file.readLine()) != null) {
                if (docId != Integer.parseInt(line.split(" ")[1])) {
                    break;
                }
            }

            offsets[i] = file.getFilePointer() - line.length() - 2;
        }

        return offsets;
    }
}
