// Mark McKelvy
// CMPS 523
// Final Project
// File: AlgorithmControlPanel.java
package agv.sim.cmps523.ui;

import static agv.sim.cmps523.ui.GuiUtils.addToWindow;
import static agv.sim.cmps523.ui.GuiUtils.centerOnScreen;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.Values;
import agv.sim.cmps523.event.JComboBoxSelectionIndexListener;
import agv.sim.cmps523.type.AlgorithmType;
import java.awt.GridBagLayout;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class AlgorithmControlPanel extends JDialog {
    private final JComboBox<String> bayesianFilterCombo;
    private final JButton closeButton;
    private final Values values;

    public AlgorithmControlPanel(Values values) {
        this.values = requireNonNull(values);
        final String[] labels = IntStream.range(0, AlgorithmType.values().length)
                .mapToObj(AlgorithmType::at)
                .map(AlgorithmType::getDescription)
                .toArray(String[]::new);
        bayesianFilterCombo = new JComboBox<>(labels);
        closeButton = new JButton("OK");
    }

    public void display() {
        bayesianFilterCombo.setSelectedIndex(0);
        bayesianFilterCombo.addActionListener(new JComboBoxSelectionIndexListener(index -> {
            final AlgorithmType algorithmType = AlgorithmType.at(index);
            values.setAlgorithmType(algorithmType);
        }));

        closeButton.addActionListener(e -> {
            final AlgorithmType algorithmType = AlgorithmType.at(bayesianFilterCombo.getSelectedIndex());
            if (!Objects.equals(AlgorithmType.NONE, algorithmType))
                dispose();
        });

        int baseX = 0;
        int baseY = 0;
        setLayout(new GridBagLayout());

        addToWindow(this, new JLabel("Choose Bayesian Filter: "), baseX, baseY, 1, 1);
        addToWindow(this, bayesianFilterCombo, baseX, baseY + 1, 1, 1);
        addToWindow(this, closeButton, baseX, baseY + 2, 1, 1);

        pack();
        centerOnScreen(this);
        setVisible(true);
    }
}
