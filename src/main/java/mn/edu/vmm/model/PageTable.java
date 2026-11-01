
package mn.edu.vmm.model;

import java.util.HashMap;
import java.util.Map;

public class PageTable {

    private final Map<Integer, Integer> pageToFrame;

    public PageTable() {
        pageToFrame = new HashMap<>();
    }

    public void mapPage(int pageNumber, int frameNumber) {
        pageToFrame.put(pageNumber, frameNumber);
    }

    public Integer getFrameNumber(int pageNumber) {
        return pageToFrame.get(pageNumber);
    }

    public boolean isPageLoaded(int pageNumber) {
        return pageToFrame.containsKey(pageNumber);
    }

    public void removePage(int pageNumber) {
        pageToFrame.remove(pageNumber);
    }

    public void clear() {
        pageToFrame.clear();
    }

    public int size() {
        return pageToFrame.size();
    }

    public Map<Integer, Integer> getMappings() {
        return Map.copyOf(pageToFrame);
    }

    @Override
    public String toString() {
        return pageToFrame.toString();
    }
}

