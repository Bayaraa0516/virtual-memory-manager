
package mn.edu.vmm;

import mn.edu.vmm.model.Frame;
import mn.edu.vmm.model.Page;
import mn.edu.vmm.model.PhysicalMemory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicalMemoryTest {

    @Test
    void shouldCreateFrames() {

        PhysicalMemory memory = new PhysicalMemory(3);

        assertEquals(3, memory.getFrameCount());

        assertTrue(memory.hasFreeFrame());
    }

    @Test
    void shouldFindFreeFrame() {

        PhysicalMemory memory = new PhysicalMemory(3);

        assertEquals(0, memory.findFreeFrame());
    }

    @Test
    void shouldLoadPageIntoFrame() {

        PhysicalMemory memory = new PhysicalMemory(3);

        byte[] data = new byte[256];
        data[0] = 42;

        memory.loadPage(0, new Page(7), data);

        assertFalse(memory.getFrame(0).isFree());

        assertEquals(
                7,
                memory.getFrame(0).getPage().getPageNumber()
        );

        assertEquals(
                42,
                memory.readByte(0, 0)
        );
    }

    @Test
    void shouldRemovePageFromFrame() {

        PhysicalMemory memory = new PhysicalMemory(3);

        byte[] data = new byte[256];

        memory.loadPage(0, new Page(7), data);
        memory.removePage(0);

        assertTrue(memory.getFrame(0).isFree());
    }

    @Test
    void shouldFindFrameContainingPage() {

        PhysicalMemory memory = new PhysicalMemory(3);

        byte[] data = new byte[256];

        memory.loadPage(1, new Page(10), data);

        assertEquals(
                1,
                memory.findFrameContainingPage(10)
        );

        assertEquals(
                -1,
                memory.findFrameContainingPage(99)
        );
    }

    @Test
    void shouldRejectInvalidFrameCount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new PhysicalMemory(0)
        );
    }
}

