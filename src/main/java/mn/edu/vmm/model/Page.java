
package mn.edu.vmm.model;

public class Page {

    private final int pageNumber;

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public String toString() {
        return "Page " + pageNumber;
    }
}
