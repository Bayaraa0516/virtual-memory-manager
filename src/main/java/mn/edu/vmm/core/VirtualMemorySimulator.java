
package mn.edu.vmm.core;

import mn.edu.vmm.model.PhysicalMemory;
import mn.edu.vmm.algorithm.PageReplacementAlgorithm;
import mn.edu.vmm.model.Frame;
import mn.edu.vmm.model.Page;
import mn.edu.vmm.model.PageTable;
import mn.edu.vmm.model.SimulationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VirtualMemorySimulator {

    private final PhysicalMemory physicalMemory;
    private final PageTable pageTable;
    private final TLB tlb;
    private final BackingStore backingStore;
    private final PageReplacementAlgorithm algorithm;
    private final AddressTranslator addressTranslator;

    private int pageFaultCount;
    private int pageHitCount;
    private int totalReferences;

    private List<Integer> referenceString;

    public VirtualMemorySimulator(
            int frameCount,
            BackingStore backingStore,
            PageReplacementAlgorithm algorithm) {

        this.physicalMemory =
                new PhysicalMemory(frameCount);

        this.pageTable =
                new PageTable();

        this.tlb =
                new TLB();

        this.backingStore =
                backingStore;

        this.algorithm =
                algorithm;

        this.addressTranslator =
                new AddressTranslator();

        this.referenceString =
                new ArrayList<>();

        pageFaultCount = 0;
        pageHitCount = 0;
        totalReferences = 0;
    }

    // =========================================
    // MEMORY ACCESS
    // =========================================

    public SimulationResult accessMemory(
            int logicalAddress) throws IOException {

        addressTranslator.validateLogicalAddress(
                logicalAddress
        );

        int pageNumber =
                addressTranslator.extractPageNumber(
                        logicalAddress
                );

        int offset =
                addressTranslator.extractOffset(
                        logicalAddress
                );

        totalReferences++;

        Integer frameNumber =
                tlb.lookup(pageNumber);

        boolean tlbHit =
                frameNumber != null;

        boolean pageFault = false;
        boolean pageHit = false;

        // Page Replacement information
        int victimPageNumber = -1;
        int victimFrameNumber = -1;

        boolean pageReplacement = false;

        // =====================================
        // TLB MISS
        // =====================================

        if (frameNumber == null) {

            frameNumber =
                    pageTable.getFrameNumber(
                            pageNumber
                    );

            // =================================
            // PAGE FAULT
            // =================================

            if (frameNumber == null) {

                pageFault = true;

                pageFaultCount++;

                // Handle Page Fault and Replacement
                int[] replacementInfo = {
                        -1, -1
                };

                frameNumber =
                        handlePageFault(
                                pageNumber,
                                replacementInfo
                        );

                // Victim information
                victimPageNumber =
                        replacementInfo[0];

                victimFrameNumber =
                        replacementInfo[1];

                // Did actual replacement occur?
                pageReplacement =
                        victimPageNumber != -1;
            }

            // =================================
            // PAGE HIT
            // =================================

            else {

                pageHit = true;

                pageHitCount++;

                // Add mapping to TLB
                tlb.addEntry(
                        pageNumber,
                        frameNumber
                );
            }

        }

        // =====================================
        // TLB HIT = PAGE HIT
        // =====================================

        else {

            pageHit = true;

            pageHitCount++;
        }

        // Update algorithm access order
        algorithm.pageAccessed(
                pageNumber
        );

        // =====================================
        // ADDRESS TRANSLATION
        // =====================================

        int physicalAddress =
                addressTranslator.createPhysicalAddress(
                        frameNumber,
                        offset
                );

        // Read actual byte from physical memory
        byte value =
                physicalMemory.readByte(
                        frameNumber,
                        offset
                );

        // =====================================
        // RETURN SIMULATION RESULT
        // =====================================

        return new SimulationResult(
                logicalAddress,
                pageNumber,
                offset,
                frameNumber,
                physicalAddress,
                value,
                pageFault,
                pageHit,
                tlbHit,
                victimPageNumber,
                victimFrameNumber,
                pageReplacement
        );
    }

    // =========================================
    // HANDLE PAGE FAULT
    // =========================================

    private int handlePageFault(
            int pageNumber,
            int[] replacementInfo) throws IOException {

        int frameNumber =
                physicalMemory.findFreeFrame();

        // =====================================
        // PAGE REPLACEMENT
        // =====================================

        if (frameNumber == -1) {

            List<Integer> loadedPages =
                    new ArrayList<>(
                            pageTable.getMappings()
                                    .keySet()
                    );

            // Algorithm chooses victim Page
            int victimPage =
                    algorithm.selectVictimPage(
                            loadedPages,
                            referenceString,
                            totalReferences - 1
                    );

            // Find victim Frame
            int victimFrame =
                    pageTable.getFrameNumber(
                            victimPage
                    );

            // Save victim information
            replacementInfo[0] =
                    victimPage;

            replacementInfo[1] =
                    victimFrame;

            // Remove old Page from Page Table
            pageTable.removePage(
                    victimPage
            );

            // Invalidate old TLB mapping
            tlb.invalidate(
                    victimPage
            );

            // Update replacement algorithm state
            algorithm.pageRemoved(
                    victimPage
            );

            // Remove old Page from Physical Memory
            physicalMemory.removePage(
                    victimFrame
            );

            // Reuse victim Frame
            frameNumber =
                    victimFrame;
        }

        // =====================================
        // LOAD NEW PAGE
        // =====================================

        // Read Page from Backing Store
        byte[] pageData =
                backingStore.readPage(
                        pageNumber
                );

        Page page =
                new Page(pageNumber);

        // Load Page into Physical Memory
        physicalMemory.loadPage(
                frameNumber,
                page,
                pageData
        );

        // Update Page Table
        pageTable.mapPage(
                pageNumber,
                frameNumber
        );

        // Update TLB
        tlb.addEntry(
                pageNumber,
                frameNumber
        );

        // Update FIFO / LRU state
        algorithm.pageLoaded(
                pageNumber
        );

        return frameNumber;
    }

    // =========================================
    // REFERENCE STRING
    // =========================================

    public void setReferenceString(
            List<Integer> referenceString) {

        this.referenceString =
                new ArrayList<>(
                        referenceString
                );
    }

    // =========================================
    // RESET
    // =========================================

    public void reset() {

        physicalMemory.clear();

        pageTable.clear();

        tlb.clear();

        algorithm.reset();

        pageFaultCount = 0;

        pageHitCount = 0;

        totalReferences = 0;
    }

    // =========================================
    // STATISTICS GETTERS
    // =========================================

    public int getPageFaultCount() {
        return pageFaultCount;
    }

    public int getPageHitCount() {
        return pageHitCount;
    }

    public int getTotalReferences() {
        return totalReferences;
    }

    public double getPageFaultRate() {

        if (totalReferences == 0) {
            return 0.0;
        }

        return (double) pageFaultCount
                / totalReferences;
    }

    public double getTlbHitRate() {
        return tlb.getHitRate();
    }

    // =========================================
    // COMPONENT GETTERS
    // =========================================

    public PhysicalMemory getPhysicalMemory() {
        return physicalMemory;
    }

    public PageTable getPageTable() {
        return pageTable;
    }

    public TLB getTLB() {
        return tlb;
    }

    public PageReplacementAlgorithm getAlgorithm() {
        return algorithm;
    }
}
