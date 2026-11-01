
package mn.edu.vmm.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.control.TableRow;

public class DashboardView extends BorderPane {

    private TextField frameCountField;
    private TextField referenceStringField;
    private ComboBox<String> algorithmBox;

    private Button startButton;
    private Button nextStepButton;
    private Button resetButton;

    private Label statusLabel;
    private Label pageFaultLabel;
    private Label pageHitLabel;
    private Label tlbHitLabel;
    private Label memoryAccessLabel;

    private Label logicalAddressLabel;
    private Label pageNumberLabel;
    private Label offsetLabel;
    private Label frameNumberLabel;
    private Label physicalAddressLabel;
    private Label pageFaultStatusLabel;

    private VBox memoryFramesBox;

    private TableView<String[]> pageTableView;
    private int animatedPageNumber = -1;
    private boolean replacementAnimation = false;
    private TableView<String[]> historyTable;

    public DashboardView() {

        setPadding(new Insets(15));

        createHeader();
        createConfigurationPanel();
        createMemoryPanel();
        createAddressTranslationPanel();
        createPageTablePanel();
        createHistoryPanel();
        createStatisticsPanel();
    }

    // =========================================
    // HEADER
    // =========================================

    private void createHeader() {

        Label title = new Label("Virtual Memory Manager");

        title.setStyle(
                "-fx-font-size: 24px;"
                        + "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Operating Systems - Virtual Memory Simulation"
        );

        VBox header = new VBox(5, title, subtitle);
        header.setPadding(new Insets(10));

        setTop(header);
    }

    // =========================================
    // SIMULATION CONFIGURATION
    // =========================================

    private void createConfigurationPanel() {

        frameCountField = new TextField("4");
        frameCountField.setPrefWidth(80);

        referenceStringField = new TextField(
                "1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5"
        );

        algorithmBox = new ComboBox<>();

        algorithmBox.getItems().addAll(
                "FIFO", "LRU", "OPT"
        );

        algorithmBox.setValue("FIFO");

        startButton = new Button("Start Simulation");
        nextStepButton = new Button("Next Step");
        resetButton = new Button("Reset");

        nextStepButton.setDisable(true);
        resetButton.setDisable(true);

        HBox frameRow = new HBox(
                10,
                new Label("Number of Frames:"),
                frameCountField
        );

        HBox algorithmRow = new HBox(
                10,
                new Label("Algorithm:"),
                algorithmBox
        );

        HBox referenceRow = new HBox(
                10,
                new Label("Reference String:"),
                referenceStringField
        );

        HBox buttons = new HBox(
                10,
                startButton,
                nextStepButton,
                resetButton
        );

        VBox configuration = new VBox(
                15,
                new Label("Simulation Configuration"),
                frameRow,
                algorithmRow,
                referenceRow,
                buttons
        );

        configuration.setPadding(new Insets(15));

        configuration.setStyle(
                "-fx-border-color: #cccccc;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
        );

        configuration.setPrefWidth(320);

        setLeft(configuration);
    }

    // =========================================
    // PHYSICAL MEMORY
    // =========================================

    private void createMemoryPanel() {

        memoryFramesBox = new VBox(10);

        Label title = new Label("Physical Memory");

        VBox memoryPanel = new VBox(
                15,
                title,
                memoryFramesBox
        );

        memoryPanel.setPadding(new Insets(15));

        setCenter(memoryPanel);
    }

    // =========================================
    // ADDRESS TRANSLATION
    // =========================================

    private void createAddressTranslationPanel() {

        logicalAddressLabel =
                new Label("Logical Address: -");

        pageNumberLabel =
                new Label("Page Number: -");

        offsetLabel =
                new Label("Offset: -");

        frameNumberLabel =
                new Label("Frame Number: -");

        physicalAddressLabel =
                new Label("Physical Address: -");

        pageFaultStatusLabel =
                new Label("Page Fault: -");

        VBox translationPanel = new VBox(
                8,
                new Label("Address Translation"),
                logicalAddressLabel,
                pageNumberLabel,
                offsetLabel,
                frameNumberLabel,
                physicalAddressLabel,
                pageFaultStatusLabel
        );

        translationPanel.setPadding(new Insets(15));

        translationPanel.setStyle(
                "-fx-border-color: #cccccc;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
        );

        setRight(translationPanel);
    }

    // =========================================
    // PAGE TABLE
    // =========================================

    private void createPageTablePanel() {

        pageTableView = new TableView<>();

        TableColumn<String[], String> pageColumn =
                new TableColumn<>("Page Number");

        pageColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[0]
                )
        );

        TableColumn<String[], String> frameColumn =
                new TableColumn<>("Frame Number");

        frameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[1]
                )
        );

        pageTableView.getColumns().addAll(
                pageColumn,
                frameColumn
        );
        pageTableView.setRowFactory(tableView -> {

            TableRow<String[]> row = new TableRow<>();

            row.itemProperty().addListener(
                    (observable, oldItem, newItem) -> {

                        if (newItem == null) {

                            row.setStyle("");

                        } else {

                            try {

                                int pageNumber =
                                        Integer.parseInt(newItem[0]);

                                if (pageNumber == animatedPageNumber) {

                                    if (replacementAnimation) {

                                        row.setStyle(
                                                "-fx-background-color: #fed7aa;"
                                                        + "-fx-border-color: #f97316;"
                                                        + "-fx-border-width: 2;"
                                        );

                                    } else {

                                        row.setStyle(
                                                "-fx-background-color: #bbf7d0;"
                                                        + "-fx-border-color: #22c55e;"
                                                        + "-fx-border-width: 2;"
                                        );
                                    }

                                } else {

                                    row.setStyle("");
                                }

                            } catch (NumberFormatException exception) {

                                row.setStyle("");
                            }
                        }
                    }
            );

            return row;
        });

        pageTableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        pageTableView.setPrefHeight(200);

        Label title = new Label("Page Table");

        VBox pageTablePanel = new VBox(
                10,
                title,
                pageTableView
        );

        pageTablePanel.setPadding(new Insets(15));

        setBottom(pageTablePanel);
    }

    // =========================================
    // SIMULATION HISTORY TABLE
    // =========================================

    private void createHistoryPanel() {

        historyTable = new TableView<>();

        // Step
        TableColumn<String[], String> stepColumn =
                new TableColumn<>("Step");

        stepColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[0]
                )
        );

        // Logical Address
        TableColumn<String[], String> logicalColumn =
                new TableColumn<>("Logical Address");

        logicalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[1]
                )
        );

        // Page
        TableColumn<String[], String> pageColumn =
                new TableColumn<>("Page");

        pageColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[2]
                )
        );

        // Offset
        TableColumn<String[], String> offsetColumn =
                new TableColumn<>("Offset");

        offsetColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[3]
                )
        );

        // Frame
        TableColumn<String[], String> frameColumn =
                new TableColumn<>("Frame");

        frameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[4]
                )
        );

        // Physical Address
        TableColumn<String[], String> physicalColumn =
                new TableColumn<>("Physical Address");

        physicalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[5]
                )
        );

        // Page Fault
        TableColumn<String[], String> faultColumn =
                new TableColumn<>("Page Fault");

        faultColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[6]
                )
        );

        // Page Hit - NEW
        TableColumn<String[], String> pageHitColumn =
                new TableColumn<>("Page Hit");

        pageHitColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[7]
                )
        );

        // TLB Hit
        TableColumn<String[], String> tlbColumn =
                new TableColumn<>("TLB Hit");

        tlbColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()[8]
                )
        );

        // Add all columns
        historyTable.getColumns().addAll(
                stepColumn,
                logicalColumn,
                pageColumn,
                offsetColumn,
                frameColumn,
                physicalColumn,
                faultColumn,
                pageHitColumn,
                tlbColumn
        );

        historyTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        historyTable.setPrefHeight(250);

        Label title = new Label("Simulation History");

        VBox historyPanel = new VBox(
                10,
                title,
                historyTable
        );

        historyPanel.setPadding(new Insets(15));

        VBox existingBottom = (VBox) getBottom();

        setBottom(new VBox(
                10,
                existingBottom,
                historyPanel
        ));
    }

    // =========================================
    // STATISTICS
    // =========================================

    private void createStatisticsPanel() {

        statusLabel =
                new Label("Status: Waiting");

        pageFaultLabel =
                new Label("Page Faults: 0");

        pageHitLabel =
                new Label("Page Hits: 0");

        tlbHitLabel =
                new Label("TLB Hits: 0");

        memoryAccessLabel =
                new Label("Memory Accesses: 0");

        VBox statistics = new VBox(
                10,
                new Label("Simulation Statistics"),
                statusLabel,
                pageFaultLabel,
                pageHitLabel,
                tlbHitLabel,
                memoryAccessLabel
        );

        statistics.setPadding(new Insets(15));

        setBottom(new VBox(
                10,
                statistics,
                getBottom()
        ));
    }

    // =========================================
    // UPDATE ADDRESS TRANSLATION
    // =========================================

    public void updateAddressTranslation(
            int logicalAddress,
            int pageNumber,
            int offset,
            int frameNumber,
            int physicalAddress,
            boolean pageFault) {

        logicalAddressLabel.setText(
                "Logical Address: " + logicalAddress
        );

        pageNumberLabel.setText(
                "Page Number: " + pageNumber
        );

        offsetLabel.setText(
                "Offset: " + offset
        );

        frameNumberLabel.setText(
                "Frame Number: " + frameNumber
        );

        physicalAddressLabel.setText(
                "Physical Address: " + physicalAddress
        );

        pageFaultStatusLabel.setText(
                "Page Fault: " + pageFault
        );
    }

    // =========================================
    // UPDATE PAGE TABLE
    // =========================================

    public void updatePageTable(
            java.util.Map<Integer, Integer> mappings) {

        pageTableView.setItems(
                FXCollections.observableArrayList()
        );

        for (var entry : mappings.entrySet()) {

            pageTableView.getItems().add(
                    new String[]{
                            String.valueOf(entry.getKey()),
                            String.valueOf(entry.getValue())
                    }
            );
        }
    }

    // =========================================
// ANIMATE PAGE TABLE ROW
// =========================================

    public void animatePageTableRow(
            int pageNumber,
            int frameNumber,
            boolean isReplacement) {

        animatedPageNumber = pageNumber;
        replacementAnimation = isReplacement;

        pageTableView.refresh();

        for (String[] row : pageTableView.getItems()) {

            if (Integer.parseInt(row[0]) == pageNumber) {

                pageTableView.getSelectionModel()
                        .select(row);

                pageTableView.scrollTo(row);

                break;
            }
        }

        TableRow<String[]> targetRow = null;

        for (int i = 0; i < pageTableView.getItems().size(); i++) {

            TableRow<String[]> row =
                    (TableRow<String[]>) pageTableView.lookup(
                            ".table-row-cell"
                    );

            if (row != null
                    && !row.isEmpty()
                    && row.getItem() != null
                    && Integer.parseInt(row.getItem()[0])
                    == pageNumber) {

                targetRow = row;
                break;
            }
        }

        if (targetRow != null) {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(250),
                            targetRow
                    );

            scale.setFromX(1.0);
            scale.setFromY(1.0);

            scale.setToX(1.04);
            scale.setToY(1.04);

            scale.setAutoReverse(true);
            scale.setCycleCount(2);

            scale.play();
        }
    }
    // =========================================
    // ADD SIMULATION HISTORY
    // =========================================

    public void addHistoryRow(
            int step,
            mn.edu.vmm.model.SimulationResult result) {

        historyTable.getItems().add(
                new String[]{
                        String.valueOf(step),
                        String.valueOf(result.getLogicalAddress()),
                        String.valueOf(result.getPageNumber()),
                        String.valueOf(result.getOffset()),
                        String.valueOf(result.getFrameNumber()),
                        String.valueOf(result.getPhysicalAddress()),
                        result.isPageFault() ? "Yes" : "No",
                        result.isPageHit() ? "Yes" : "No",
                        result.isTlbHit() ? "Yes" : "No"
                }
        );
    }

    // =========================================
    // CLEAR HISTORY
    // =========================================

    public void clearHistory() {

        historyTable.getItems().clear();
    }

    // =========================================
    // GETTERS
    // =========================================

    public TextField getFrameCountField() {
        return frameCountField;
    }

    public TextField getReferenceStringField() {
        return referenceStringField;
    }

    public ComboBox<String> getAlgorithmBox() {
        return algorithmBox;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getNextStepButton() {
        return nextStepButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public Label getPageFaultLabel() {
        return pageFaultLabel;
    }

    public Label getPageHitLabel() {
        return pageHitLabel;
    }

    public Label getTlbHitLabel() {
        return tlbHitLabel;
    }

    public Label getMemoryAccessLabel() {
        return memoryAccessLabel;
    }

    public VBox getMemoryFramesBox() {
        return memoryFramesBox;
    }
}
