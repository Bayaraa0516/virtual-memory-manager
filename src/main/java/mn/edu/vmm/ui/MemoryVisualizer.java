
package mn.edu.vmm.ui;

import javafx.scene.layout.Pane;

public class MemoryVisualizer {

    private final Pane root;

    public MemoryVisualizer() {
        root = new Pane();

        // Physical memory visualization
        // and page/frame animation will be implemented later.
    }

    public Pane getRoot() {
        return root;
    }
}
