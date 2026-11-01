
package mn.edu.vmm.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FIFOAlgorithm implements PageReplacementAlgorithm {

    private final List<Integer> insertionOrder;

    public FIFOAlgorithm() {
        insertionOrder = new ArrayList<>();
    }

    @Override
    public int selectVictimPage(
            List<Integer> loadedPages,
            List<Integer> referenceString,
            int currentIndex
    ) {

        for (int page : insertionOrder) {
            if (loadedPages.contains(page)) {
                return page;
            }
        }

        if (loadedPages.isEmpty()) {
            throw new IllegalStateException(
                    "No pages available for replacement."
            );
        }

        return loadedPages.get(0);
    }

    @Override
    public void pageLoaded(int pageNumber) {
        insertionOrder.remove(Integer.valueOf(pageNumber));
        insertionOrder.add(pageNumber);
    }

    @Override
    public void pageRemoved(int pageNumber) {
        insertionOrder.remove(Integer.valueOf(pageNumber));
    }

    @Override
    public void pageAccessed(int pageNumber) {
        // FIFO-д хандалтын дараалал өөрчлөгдөхгүй.
    }

    @Override
    public void reset() {
        insertionOrder.clear();
    }

    @Override
    public String getName() {
        return "FIFO";
    }

    public List<Integer> getInsertionOrder() {
        return List.copyOf(insertionOrder);
    }
}
