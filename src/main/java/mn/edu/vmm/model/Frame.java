
package mn.edu.vmm.model;

import java.util.Arrays;

public class Frame {

    public static final int FRAME_SIZE = 256;

    private final int frameNumber;
    private Page page;
    private final byte[] data;

    public Frame(int frameNumber) {
        this.frameNumber = frameNumber;
        this.page = null;
        this.data = new byte[FRAME_SIZE];
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public Page getPage() {
        return page;
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public byte readByte(int offset) {
        validateOffset(offset);
        return data[offset];
    }

    public boolean isFree() {
        return page == null;
    }

    public void loadPage(Page page, byte[] pageData) {

        if (pageData.length != FRAME_SIZE) {
            throw new IllegalArgumentException(
                    "Page data must contain exactly 256 bytes.");
        }

        this.page = page;

        System.arraycopy(
                pageData, 0,
                data, 0,
                FRAME_SIZE
        );
    }

    public void removePage() {
        this.page = null;
        Arrays.fill(data, (byte) 0);
    }

    private void validateOffset(int offset) {
        if (offset < 0 || offset >= FRAME_SIZE) {
            throw new IllegalArgumentException(
                    "Offset must be between 0 and 255.");
        }
    }

    @Override
    public String toString() {
        return "Frame " + frameNumber
                + " -> "
                + (page == null ? "Empty" : page.toString());
    }
}

