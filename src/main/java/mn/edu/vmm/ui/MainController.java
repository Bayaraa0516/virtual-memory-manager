
package mn.edu.vmm.ui;

import mn.edu.vmm.algorithm.*;
import mn.edu.vmm.core.BackingStore;
import mn.edu.vmm.core.VirtualMemorySimulator;
import mn.edu.vmm.model.Frame;
import mn.edu.vmm.model.SimulationResult;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainController {

    private final DashboardView view;

    private VirtualMemorySimulator simulator;

    private List<Integer> referenceString;
    private int currentIndex;

    // Animation control
    private Animation currentAnimation;
    private boolean animationRunning = false;

    public MainController(DashboardView view) {
        this.view = view;
        initialize();
    }

    // =========================================
    // INITIALIZE
    // =========================================

    private void initialize() {

        view.getStartButton().setOnAction(event ->
                startSimulation()
        );

        view.getNextStepButton().setOnAction(event ->
                nextStep()
        );

        view.getResetButton().setOnAction(event ->
                resetSimulation()
        );
    }

    // =========================================
    // START SIMULATION
    // =========================================

    private void startSimulation() {

        if (animationRunning) {
            return;
        }

        try {

            int frameCount = Integer.parseInt(
                    view.getFrameCountField()
                            .getText()
                            .trim()
            );

            if (frameCount <= 0) {
                showError(
                        "Frame count must be greater than 0."
                );
                return;
            }

            referenceString = parseReferenceString(
                    view.getReferenceStringField().getText()
            );

            if (referenceString.isEmpty()) {
                showError(
                        "Reference String cannot be empty."
                );
                return;
            }

            String algorithmName =
                    view.getAlgorithmBox().getValue();

            PageReplacementAlgorithm algorithm =
                    createAlgorithm(algorithmName);

            BackingStore backingStore =
                    new BackingStore("BACKING_STORE.bin");

            simulator = new VirtualMemorySimulator(
                    frameCount,
                    backingStore,
                    algorithm
            );

            simulator.setReferenceString(
                    referenceString
            );

            currentIndex = 0;

            // Clear previous data
            view.clearHistory();

            view.updatePageTable(
                    Collections.emptyMap()
            );

            view.getMemoryFramesBox()
                    .getChildren()
                    .clear();

            // Button states
            view.getStartButton().setDisable(true);
            view.getNextStepButton().setDisable(false);
            view.getResetButton().setDisable(false);

            view.getStatusLabel().setText(
                    "Status: Simulation Started"
            );

            refreshMemoryFrames();

            // Process first reference automatically
            nextStep();

        } catch (NumberFormatException exception) {

            showError(
                    "Frame count must be a valid integer."
            );

        } catch (IllegalArgumentException exception) {

            showError(exception.getMessage());

        } catch (Exception exception) {

            showError(
                    "Could not start simulation: "
                            + exception.getMessage()
            );
        }
    }

    // =========================================
    // NEXT STEP
    // =========================================

    private void nextStep() {

        // Prevent overlapping animation and simulation
        if (animationRunning) {
            return;
        }

        if (simulator == null) {

            showError(
                    "Please start the simulation first."
            );
            return;
        }

        if (currentIndex >= referenceString.size()) {

            view.getStatusLabel().setText(
                    "Status: Simulation Completed"
            );

            view.getNextStepButton().setDisable(true);
            return;
        }

        try {

            int pageNumber =
                    referenceString.get(currentIndex);

            int logicalAddress =
                    pageNumber * 256;

            SimulationResult result =
                    simulator.accessMemory(
                            logicalAddress
                    );

            currentIndex++;

            // Update statistics and tables
            updateStatistics(result);

            // Update physical memory visualization
            refreshMemoryFrames(result);

            view.getStatusLabel().setText(
                    "Status: Access "
                            + currentIndex
                            + " / "
                            + referenceString.size()
            );

            // Lock Next Step during visual effects
            if (result.isPageReplacement()
                    || result.isPageFault()
                    || result.isPageHit()
                    || result.isTlbHit()) {

                animationRunning = true;
                view.getNextStepButton().setDisable(true);

            } else if (currentIndex >= referenceString.size()) {

                view.getNextStepButton().setDisable(true);

                view.getStatusLabel().setText(
                        "Status: Simulation Completed"
                );

            } else {

                view.getNextStepButton().setDisable(false);
            }

        } catch (IOException exception) {

            showError(
                    "Backing Store error: "
                            + exception.getMessage()
            );

            view.getNextStepButton().setDisable(false);
        }
    }

    // =========================================
    // UPDATE STATISTICS AND TABLES
    // =========================================

    private void updateStatistics(
            SimulationResult result) {

        view.getPageFaultLabel().setText(
                "Page Faults: "
                        + simulator.getPageFaultCount()
        );

        view.getPageHitLabel().setText(
                "Page Hits: "
                        + simulator.getPageHitCount()
        );

        view.getTlbHitLabel().setText(
                "TLB Hits: "
                        + simulator.getTLB().getHitCount()
        );

        view.getMemoryAccessLabel().setText(
                "Memory Accesses: "
                        + simulator.getTotalReferences()
        );

        // Address translation
        view.updateAddressTranslation(
                result.getLogicalAddress(),
                result.getPageNumber(),
                result.getOffset(),
                result.getFrameNumber(),
                result.getPhysicalAddress(),
                result.isPageFault()
        );

        // Page Table
        view.updatePageTable(
                simulator.getPageTable().getMappings()
        );

        // Animate accessed Page Table row
        if (result.isPageFault()) {

            view.animatePageTableRow(
                    result.getPageNumber(),
                    result.getFrameNumber(),
                    result.isPageReplacement()
            );
        }

        // Simulation history
        view.addHistoryRow(
                currentIndex,
                result
        );

        // Console output
        System.out.println(
                "Logical Address: "
                        + result.getLogicalAddress()
        );

        System.out.println(
                "Page Number: "
                        + result.getPageNumber()
        );

        System.out.println(
                "Offset: "
                        + result.getOffset()
        );

        System.out.println(
                "Frame Number: "
                        + result.getFrameNumber()
        );

        System.out.println(
                "Physical Address: "
                        + result.getPhysicalAddress()
        );

        System.out.println(
                "Page Fault: "
                        + result.isPageFault()
        );

        System.out.println(
                "Page Hit: "
                        + result.isPageHit()
        );

        System.out.println(
                "TLB Hit: "
                        + result.isTlbHit()
        );

        if (result.isPageReplacement()) {

            System.out.println(
                    "Victim Page: "
                            + result.getVictimPageNumber()
            );

            System.out.println(
                    "Victim Frame: "
                            + result.getVictimFrameNumber()
            );
        }
    }

    // =========================================
    // MEMORY VISUALIZATION
    // =========================================

    private void refreshMemoryFrames() {

        refreshMemoryFrames(null);
    }

    private void refreshMemoryFrames(
            SimulationResult result) {

        VBox memoryBox =
                view.getMemoryFramesBox();

        memoryBox.getChildren().clear();

        if (simulator == null) {
            return;
        }

        int accessedFrame = -1;

        if (result != null) {

            accessedFrame =
                    result.getFrameNumber();
        }

        for (Frame frame :
                simulator.getPhysicalMemory().getFrames()) {

            int frameNumber =
                    frame.getFrameNumber();

            String content;

            if (frame.getPage() == null) {

                content = "Empty";

            } else {

                content = "Page "
                        + frame.getPage().getPageNumber();
            }

            Label frameLabel = new Label(
                    "Frame "
                            + frameNumber
                            + "  →  "
                            + content
            );

            frameLabel.setPrefWidth(300);
            frameLabel.setPrefHeight(40);

            String defaultStyle =
                    "-fx-padding: 10;"
                            + "-fx-background-color: #dbeafe;"
                            + "-fx-border-color: #93c5fd;"
                            + "-fx-border-radius: 5;"
                            + "-fx-background-radius: 5;";

            frameLabel.setStyle(defaultStyle);

            // PAGE REPLACEMENT
            if (result != null
                    && result.isPageReplacement()
                    && frameNumber
                    == result.getVictimFrameNumber()) {

                animatePageReplacement(
                        frameLabel,
                        result
                );
            }

            // PAGE FAULT
            else if (result != null
                    && result.isPageFault()
                    && frameNumber == accessedFrame) {

                animatePageFault(frameLabel);
            }

            // PAGE HIT
            else if (result != null
                    && result.isPageHit()
                    && frameNumber == accessedFrame) {

                animatePageHit(frameLabel);
            }

            // TLB HIT
            if (result != null
                    && result.isTlbHit()
                    && frameNumber == accessedFrame) {

                animateTlbHit(frameLabel);
            }

            memoryBox.getChildren().add(
                    frameLabel
            );
        }
    }

    // =========================================
    // PAGE REPLACEMENT TRANSITION
    // =========================================

    private void animatePageReplacement(
            Label frameLabel,
            SimulationResult result) {

        int victimPage =
                result.getVictimPageNumber();

        int newPage =
                result.getPageNumber();

        int frameNumber =
                result.getVictimFrameNumber();

        animationRunning = true;

        view.getNextStepButton().setDisable(true);

        // Stage 1: Victim Page exits
        frameLabel.setText(
                "Frame " + frameNumber
                        + "  →  Page " + victimPage
                        + " (Removing...)"
        );

        frameLabel.setStyle(
                "-fx-padding: 10;"
                        + "-fx-background-color: #fecaca;"
                        + "-fx-border-color: #ef4444;"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 5;"
                        + "-fx-background-radius: 5;"
        );

        TranslateTransition moveOut =
                new TranslateTransition(
                        Duration.millis(400),
                        frameLabel
                );

        moveOut.setFromX(0);
        moveOut.setToX(-350);

        FadeTransition fadeOut =
                new FadeTransition(
                        Duration.millis(400),
                        frameLabel
                );

        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ParallelTransition exitAnimation =
                new ParallelTransition(
                        moveOut,
                        fadeOut
                );

        // Stage 2: New Page enters
        TranslateTransition moveIn =
                new TranslateTransition(
                        Duration.millis(500),
                        frameLabel
                );

        moveIn.setFromX(350);
        moveIn.setToX(0);

        FadeTransition fadeIn =
                new FadeTransition(
                        Duration.millis(500),
                        frameLabel
                );

        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ParallelTransition enterAnimation =
                new ParallelTransition(
                        moveIn,
                        fadeIn
                );

        // Stage 3: Show Empty Frame
        FadeTransition emptyTransition =
                new FadeTransition(
                        Duration.millis(150),
                        frameLabel
                );

        emptyTransition.setFromValue(0.0);
        emptyTransition.setToValue(1.0);

        // Stage 4: Exit animation completed
        exitAnimation.setOnFinished(event -> {

            if (!animationRunning) {
                return;
            }

            frameLabel.setText(
                    "Frame " + frameNumber
                            + "  →  Empty"
            );

            frameLabel.setStyle(
                    "-fx-padding: 10;"
                            + "-fx-background-color: #f3f4f6;"
                            + "-fx-border-color: #9ca3af;"
                            + "-fx-border-width: 2;"
                            + "-fx-border-radius: 5;"
                            + "-fx-background-radius: 5;"
            );

            frameLabel.setTranslateX(0);
            frameLabel.setOpacity(1.0);

            // Stage 5: New Page arrives
            frameLabel.setText(
                    "Frame " + frameNumber
                            + "  →  Page " + newPage
            );

            frameLabel.setStyle(
                    "-fx-padding: 10;"
                            + "-fx-background-color: #fed7aa;"
                            + "-fx-border-color: #f97316;"
                            + "-fx-border-width: 2;"
                            + "-fx-border-radius: 5;"
                            + "-fx-background-radius: 5;"
            );

            enterAnimation.play();
        });

        // Stage 6: Animation completed
        enterAnimation.setOnFinished(event -> {

            if (!animationRunning) {
                return;
            }

            frameLabel.setTranslateX(0);
            frameLabel.setOpacity(1.0);

            finishAnimation();
        });

        // Start animation
        currentAnimation = exitAnimation;

        exitAnimation.play();
    }

    // =========================================
    // PAGE FAULT ANIMATION
    // =========================================

    private void animatePageFault(
            Label frameLabel) {

        frameLabel.setStyle(
                "-fx-padding: 10;"
                        + "-fx-background-color: #fed7aa;"
                        + "-fx-border-color: #f97316;"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 5;"
                        + "-fx-background-radius: 5;"
        );

        FadeTransition animation =
                new FadeTransition(
                        Duration.millis(350),
                        frameLabel
                );

        animation.setFromValue(0.4);
        animation.setToValue(1.0);
        animation.setCycleCount(2);
        animation.setAutoReverse(true);

        animation.setOnFinished(event ->
                finishAnimation()
        );

        currentAnimation = animation;
        animation.play();
    }

    // =========================================
    // PAGE HIT ANIMATION
    // =========================================

    private void animatePageHit(
            Label frameLabel) {

        frameLabel.setStyle(
                "-fx-padding: 10;"
                        + "-fx-background-color: #bbf7d0;"
                        + "-fx-border-color: #22c55e;"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 5;"
                        + "-fx-background-radius: 5;"
        );

        FadeTransition animation =
                new FadeTransition(
                        Duration.millis(300),
                        frameLabel
                );

        animation.setFromValue(0.5);
        animation.setToValue(1.0);
        animation.setCycleCount(2);
        animation.setAutoReverse(true);

        animation.setOnFinished(event ->
                finishAnimation()
        );

        currentAnimation = animation;
        animation.play();
    }

    // =========================================
    // TLB HIT ANIMATION
    // =========================================

    private void animateTlbHit(
            Label frameLabel) {

        frameLabel.setStyle(
                "-fx-padding: 10;"
                        + "-fx-background-color: #bfdbfe;"
                        + "-fx-border-color: #2563eb;"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 5;"
                        + "-fx-background-radius: 5;"
        );

        FadeTransition animation =
                new FadeTransition(
                        Duration.millis(200),
                        frameLabel
                );

        animation.setFromValue(0.3);
        animation.setToValue(1.0);
        animation.setCycleCount(2);
        animation.setAutoReverse(true);

        animation.setOnFinished(event ->
                finishAnimation()
        );

        currentAnimation = animation;
        animation.play();
    }

    // =========================================
    // FINISH ANIMATION
    // =========================================

    private void finishAnimation() {

        animationRunning = false;
        currentAnimation = null;

        if (currentIndex < referenceString.size()) {

            view.getNextStepButton().setDisable(false);

        } else {

            view.getNextStepButton().setDisable(true);

            view.getStatusLabel().setText(
                    "Status: Simulation Completed"
            );
        }
    }

    // =========================================
    // STOP CURRENT ANIMATION
    // =========================================

    private void stopCurrentAnimation() {

        animationRunning = false;

        if (currentAnimation != null) {

            currentAnimation.stop();

            currentAnimation = null;
        }
    }

    // =========================================
    // RESET SIMULATION
    // =========================================

    private void resetSimulation() {

        stopCurrentAnimation();

        if (simulator != null) {
            simulator.reset();
        }

        simulator = null;

        referenceString =
                new ArrayList<>();

        currentIndex = 0;

        // Clear Physical Memory
        view.getMemoryFramesBox()
                .getChildren()
                .clear();

        // Reset Statistics
        view.getStatusLabel().setText(
                "Status: Waiting"
        );

        view.getPageFaultLabel().setText(
                "Page Faults: 0"
        );

        view.getPageHitLabel().setText(
                "Page Hits: 0"
        );

        view.getTlbHitLabel().setText(
                "TLB Hits: 0"
        );

        view.getMemoryAccessLabel().setText(
                "Memory Accesses: 0"
        );

        // Clear Page Table
        view.updatePageTable(
                Collections.emptyMap()
        );

        // Clear History
        view.clearHistory();

        // Reset Address Translation
        view.updateAddressTranslation(
                0, 0, 0, 0, 0, false
        );

        // Reset Buttons
        view.getStartButton().setDisable(false);

        view.getNextStepButton().setDisable(true);

        view.getResetButton().setDisable(true);
    }

    // =========================================
    // PARSE REFERENCE STRING
    // =========================================

    private List<Integer> parseReferenceString(
            String input) {

        List<Integer> pages =
                new ArrayList<>();

        String[] tokens =
                input.trim().split("[,\\s]+");

        for (String token : tokens) {

            if (token.isBlank()) {
                continue;
            }

            int page =
                    Integer.parseInt(token);

            if (page < 0 || page > 255) {

                throw new IllegalArgumentException(
                        "Page numbers must be between 0 and 255."
                );
            }

            pages.add(page);
        }

        return pages;
    }

    // =========================================
    // ALGORITHM FACTORY
    // =========================================

    private PageReplacementAlgorithm createAlgorithm(
            String algorithmName) {

        return switch (algorithmName) {

            case "FIFO" ->
                    new FIFOAlgorithm();

            case "LRU" ->
                    new LRUAlgorithm();

            case "OPT" ->
                    new OptimalAlgorithm();

            default ->
                    throw new IllegalArgumentException(
                            "Unknown page replacement algorithm."
                    );
        };
    }

    // =========================================
    // ERROR MESSAGE
    // =========================================

    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Simulation Error"
        );

        alert.setHeaderText(
                "Something went wrong"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }
}
