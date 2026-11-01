
package mn.edu.vmm.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translation Lookaside Buffer.
 * Page number болон Frame number-ийн mapping хадгална.
 */
public class TLB {

    private static final int TLB_SIZE = 16;

    private final Map<Integer, Integer> entries;

    private int hitCount;
    private int missCount;

    public TLB() {
        entries = new LinkedHashMap<>();
    }

    public Integer lookup(int pageNumber) {
        if (entries.containsKey(pageNumber)) {
            hitCount++;
            return entries.get(pageNumber);
        }

        missCount++;
        return null;
    }

    public void addEntry(int pageNumber, int frameNumber) {

        // Өмнө байсан mapping-ийг шинэчилнэ.
        entries.remove(pageNumber);

        // TLB дүүрсэн бол хамгийн эрт нэмэгдсэн entry-г устгана.
        if (entries.size() >= TLB_SIZE) {
            Integer oldestPage = entries.keySet()
                    .iterator()
                    .next();

            entries.remove(oldestPage);
        }

        entries.put(pageNumber, frameNumber);
    }

    public void invalidate(int pageNumber) {
        entries.remove(pageNumber);
    }

    public void clear() {
        entries.clear();
        hitCount = 0;
        missCount = 0;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getMissCount() {
        return missCount;
    }

    public int getSize() {
        return entries.size();
    }

    public double getHitRate() {

        int total = hitCount + missCount;

        if (total == 0) {
            return 0.0;
        }

        return (double) hitCount / total;
    }

    public Map<Integer, Integer> getEntries() {
        return Map.copyOf(entries);
    }
}
