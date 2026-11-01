
package mn.edu.vmm.core;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BackingStore {

    private static final int PAGE_SIZE = 256;

    private final String filePath;

    public BackingStore(String filePath) {
        this.filePath = filePath;
    }

    public byte[] readPage(int pageNumber) throws IOException {

        byte[] buffer = new byte[PAGE_SIZE];

        try (RandomAccessFile file =
                     new RandomAccessFile(filePath, "r")) {

            file.seek((long) pageNumber * PAGE_SIZE);
            file.readFully(buffer);
        }

        return buffer;
    }
}
