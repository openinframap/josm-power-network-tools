package org.openinframap.powernetworktools;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;

import javax.swing.*;
import java.awt.*;

public class VoltageEstimatorDialog extends ToggleDialog {
    private final JTextArea resultTextArea;

    public VoltageEstimatorDialog() {
        super("Voltage estimator",
                "voltage",
                "Voltage estimator",
                null,
                100);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create result display area
        resultTextArea = new JTextArea(3, 20);
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        createLayout(mainPanel, false, null);
    }

    /**
     * Display the estimated voltage based on distance across the busbar.
     */
    public void setVoltageDistance(double distance) {
        double v = calculateVoltageFromDistance(distance);

        StringBuilder result = new StringBuilder();
        result.append("Measured distance: ").append(String.format("%.2f", distance)).append(" meters\n");
        result.append("Phase-phase clearance: ").append(String.format("%.2f", distance / 2)).append(" meters\n");

        if (v < 75) {
            result.append("Estimated voltage: below 75 kV\n");
        } else if (v > 700) {
            result.append("Estimated voltage: above 700 kV\n");
        } else {
            result.append("Estimated voltage: ").append(String.format("%.0f", v)).append(" kV");
        }

        resultTextArea.setText(result.toString());
    }

    /**
     * Clear the displayed voltage.
     */
    public void clearVoltage() {
        resultTextArea.setText("");
    }

    /**
     * Calculate voltage in kilovolts from distance across busbar (between outer two
     * phase wires).
     * This currently uses a linear fit of a number of measurements taken worldwide.
     */
    private double calculateVoltageFromDistance(double distance) {
        double phase_distance = distance / 2;
        return (phase_distance - 0.6776) / 0.0131;
    }
}