
package mn.edu.vmm.algorithm;

import java.util.List;

public class OptimalAlgorithm implements PageReplacementAlgorithm {

    @Override
    public int selectVictimPage(
            List<Integer> loadedPages,
            List<Integer> referenceString,
            int currentIndex
    ) {

        int victimPage = -1;
        int farthestDistance = -1;

        for (int page : loadedPages) {

            int nextUse = -1;

            // Одоогийн index-ийн дараах reference-үүдийг хайна.
            for (int i = currentIndex + 1;
                 i < referenceString.size();
                 i++) {

                if (referenceString.get(i) == page) {
                    nextUse = i;
                    break;
                }
            }

            // Цааш дахин ашиглагдахгүй Page.
            if (nextUse == -1) {
                return page;
            }

            if (nextUse > farthestDistance) {
                farthestDistance = nextUse;
                victimPage = page;
            }
        }

        if (victimPage == -1) {
            throw new IllegalStateException(
                    "No pages available for replacement."
            );
        }

        return victimPage;
    }

    @Override
    public void pageLoaded(int pageNumber) {
    }

    @Override
    public void pageRemoved(int pageNumber) {
    }

    @Override
    public void pageAccessed(int pageNumber) {
    }

    @Override
    public void reset() {
    }

    @Override
    public String getName() {
        return "Optimal";
    }
}
