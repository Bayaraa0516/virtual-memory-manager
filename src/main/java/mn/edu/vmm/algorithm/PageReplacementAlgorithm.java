
package mn.edu.vmm.algorithm;

import java.util.List;

public interface PageReplacementAlgorithm {

    int selectVictimPage(
            List<Integer> loadedPages,
            List<Integer> referenceString,
            int currentIndex
    );

    void pageLoaded(int pageNumber);

    void pageRemoved(int pageNumber);

    void pageAccessed(int pageNumber);

    void reset();

    String getName();
}
