
package mn.edu.vmm.model;

public class SimulationResult {

    private final int logicalAddress;
    private final int pageNumber;
    private final int offset;
    private final int frameNumber;
    private final int physicalAddress;
    private final byte value;

    private final boolean pageFault;
    private final boolean pageHit;
    private final boolean tlbHit;

    // Page Replacement information
    private final int victimPageNumber;
    private final int victimFrameNumber;
    private final boolean pageReplacement;

    public SimulationResult(
            int logicalAddress,
            int pageNumber,
            int offset,
            int frameNumber,
            int physicalAddress,
            byte value,
            boolean pageFault,
            boolean pageHit,
            boolean tlbHit,
            int victimPageNumber,
            int victimFrameNumber,
            boolean pageReplacement) {

        this.logicalAddress = logicalAddress;
        this.pageNumber = pageNumber;
        this.offset = offset;
        this.frameNumber = frameNumber;
        this.physicalAddress = physicalAddress;
        this.value = value;

        this.pageFault = pageFault;
        this.pageHit = pageHit;
        this.tlbHit = tlbHit;

        this.victimPageNumber = victimPageNumber;
        this.victimFrameNumber = victimFrameNumber;
        this.pageReplacement = pageReplacement;
    }

    public int getLogicalAddress() {
        return logicalAddress;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getOffset() {
        return offset;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getPhysicalAddress() {
        return physicalAddress;
    }

    public byte getValue() {
        return value;
    }

    public boolean isPageFault() {
        return pageFault;
    }

    public boolean isPageHit() {
        return pageHit;
    }

    public boolean isTlbHit() {
        return tlbHit;
    }

    // =========================================
    // PAGE REPLACEMENT GETTERS
    // =========================================

    public int getVictimPageNumber() {
        return victimPageNumber;
    }

    public int getVictimFrameNumber() {
        return victimFrameNumber;
    }

    public boolean isPageReplacement() {
        return pageReplacement;
    }
}
