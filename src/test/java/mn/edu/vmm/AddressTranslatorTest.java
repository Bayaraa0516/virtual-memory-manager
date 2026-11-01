
package mn.edu.vmm;

import mn.edu.vmm.core.AddressTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTranslatorTest {

    private AddressTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new AddressTranslator();
    }

    @Test
    void shouldExtractPageNumber() {

        int logicalAddress = 1025;

        assertEquals(
                4,
                translator.extractPageNumber(logicalAddress)
        );
    }

    @Test
    void shouldExtractOffset() {

        int logicalAddress = 1025;

        assertEquals(
                1,
                translator.extractOffset(logicalAddress)
        );
    }

    @Test
    void shouldCreatePhysicalAddress() {

        int frameNumber = 2;
        int offset = 1;

        assertEquals(
                513,
                translator.createPhysicalAddress(
                        frameNumber, offset)
        );
    }

    @Test
    void shouldHandleMaximumLogicalAddress() {

        int logicalAddress = 65535;

        assertEquals(
                255,
                translator.extractPageNumber(logicalAddress)
        );

        assertEquals(
                255,
                translator.extractOffset(logicalAddress)
        );
    }

    @Test
    void shouldRejectNegativeLogicalAddress() {

        assertThrows(
                IllegalArgumentException.class,
                () -> translator.extractPageNumber(-1)
        );
    }

    @Test
    void shouldRejectAddressGreaterThan65535() {

        assertThrows(
                IllegalArgumentException.class,
                () -> translator.extractOffset(65536)
        );
    }
}

