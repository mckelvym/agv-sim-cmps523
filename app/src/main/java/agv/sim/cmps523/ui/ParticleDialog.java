// Mark McKelvy
// CMPS 523
// Final Project
// File: ParticleDialog.java
package agv.sim.cmps523.ui;

import static agv.sim.cmps523.ui.GuiUtils.addToWindow;
import static agv.sim.cmps523.ui.GuiUtils.centerOnScreen;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.Values;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public final class ParticleDialog extends JDialog {
    private final JComboBox<String> numParticlesCombo;
    private final JButton closeButton;
    private final Values values;

    public ParticleDialog(Values values) {
        this.values = requireNonNull(values);
        numParticlesCombo = new JComboBox<>(new String[]{"100", "1000", "10000"});
        closeButton = new JButton("OK");
    }

    public void display() {
        numParticlesCombo.setSelectedIndex(0);
        closeButton.addActionListener(e -> onClose());

        int baseX = 0;
        int baseY = 0;
        setLayout(new GridBagLayout());

        addToWindow(this, new JLabel("Choose Number of Particles: "), baseX, baseY, 1, 1);
        addToWindow(this, numParticlesCombo, baseX, baseY + 1, 1, 1);
        addToWindow(this, closeButton, baseX, baseY + 2, 1, 1);

        pack();
        centerOnScreen(this);
        setVisible(true);
    }

    private void onClose() {
        final Object selectedItem = numParticlesCombo.getSelectedItem();
        values.setNumberOfParticles(parseInt((String.valueOf(selectedItem))));
        dispose();
    }
}
