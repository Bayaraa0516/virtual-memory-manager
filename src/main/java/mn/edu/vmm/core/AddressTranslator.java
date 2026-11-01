
package mn.edu.vmm.core;

public class AddressTranslator {

    public static final int PAGE_SIZE = 256;
    public static final int PAGE_NUMBER_BITS = 8;
    public static final int OFFSET_BITS = 8;

    public static final int MAX_LOGICAL_ADDRESS = 65535;
    public static final int NUMBER_OF_PAGES = 256;

    public int extractPageNumber(int logicalAddress) {

        validateLogicalAddress(logicalAddress);

        return (logicalAddress >> OFFSET_BITS) & 0xFF;
    }

    public int extractOffset(int logicalAddress) {

        validateLogicalAddress(logicalAddress);

        return logicalAddress & 0xFF;
    }

    public int createPhysicalAddress(
            int frameNumber,
            int offset) {

        if (frameNumber < 0 || frameNumber > 255) {
            throw new IllegalArgumentException(
                    "Frame number must be between 0 and 255.");
        }

        if (offset < 0 || offset > 255) {
            throw new IllegalArgumentException(
                    "Offset must be between 0 and 255.");
        }

        return (frameNumber << OFFSET_BITS) | offset;
    }

    public void validateLogicalAddress(int logicalAddress) {

        if (logicalAddress < 0 ||
                logicalAddress > MAX_LOGICAL_ADDRESS) {

            throw new IllegalArgumentException(
                    "Logical address must be between 0 and 65535.");
        }
    }
}
