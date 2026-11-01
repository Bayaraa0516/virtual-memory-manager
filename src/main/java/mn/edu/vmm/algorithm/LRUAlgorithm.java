
package mn.edu.vmm.algorithm;

import java.util.ArrayList;
import java.util.List;

public class LRUAlgorithm implements PageReplacementAlgorithm {

    private final List<Integer> usageOrder;

    public LRUAlgorithm() {
        usageOrder = new ArrayList<>();
    }

    @Override
    public int selectVictimPage(
            List<Integer> loadedPages,
            List<Integer> referenceString,
            int currentIndex
    ) {

        for (int page : usageOrder) {
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
        pageAccessed(pageNumber);
    }

    @Override
    public void pageRemoved(int pageNumber) {
        usageOrder.remove(Integer.valueOf(pageNumber));
    }

    @Override
    public void pageAccessed(int pageNumber) {
        usageOrder.remove(Integer.valueOf(pageNumber));
        usageOrder.add(pageNumber);
    }

    @Override
    public void reset() {
        usageOrder.clear();
    }

    @Override
    public String getName() {
        return "LRU";
    }

    public List<Integer> getUsageOrder() {
        return List.copyOf(usageOrder);
    }
}
