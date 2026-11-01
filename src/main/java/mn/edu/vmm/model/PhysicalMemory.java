
package mn.edu.vmm.model;

import java.util.ArrayList;
import java.util.List;

public class PhysicalMemory {

    private final List<Frame> frames;

    public PhysicalMemory(int frameCount) {

        if (frameCount <= 0) {
            throw new IllegalArgumentException(
                    "Frame count must be greater than zero.");
        }

        frames = new ArrayList<>();

        for (int i = 0; i < frameCount; i++) {
            frames.add(new Frame(i));
        }
    }

    public int getFrameCount() {
        return frames.size();
    }

    public Frame getFrame(int frameNumber) {
        validateFrameNumber(frameNumber);
        return frames.get(frameNumber);
    }

    public List<Frame> getFrames() {
        return List.copyOf(frames);
    }

    public int findFreeFrame() {

        for (Frame frame : frames) {
            if (frame.isFree()) {
                return frame.getFrameNumber();
            }
        }

        return -1;
    }

    public boolean hasFreeFrame() {
        return findFreeFrame() != -1;
    }

    public void loadPage(
            int frameNumber,
            Page page,
            byte[] pageData) {

        Frame frame = getFrame(frameNumber);

        if (!frame.isFree()) {
            throw new IllegalStateException(
                    "Frame is already occupied.");
        }

        frame.loadPage(page, pageData);
    }

    public void removePage(int frameNumber) {
        getFrame(frameNumber).removePage();
    }

    public int findFrameContainingPage(int pageNumber) {

        for (Frame frame : frames) {

            Page page = frame.getPage();

            if (page != null &&
                    page.getPageNumber() == pageNumber) {

                return frame.getFrameNumber();
            }
        }

        return -1;
    }

    public byte readByte(int frameNumber, int offset) {
        return getFrame(frameNumber).readByte(offset);
    }

    public void clear() {

        for (Frame frame : frames) {
            frame.removePage();
        }
    }

    private void validateFrameNumber(int frameNumber) {

        if (frameNumber < 0 ||
                frameNumber >= frames.size()) {

            throw new IllegalArgumentException(
                    "Invalid frame number: " + frameNumber);
        }
    }

    @Override
    public String toString() {
        return frames.toString();
    }
}
