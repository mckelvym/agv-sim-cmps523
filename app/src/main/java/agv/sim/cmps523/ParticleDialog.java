// Mark McKelvy
// CMPS 523
// Final Project
// File: ParticleDialog.java
package agv.sim.cmps523;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

class ParticleDialog extends JDialog {
    static String[] numParticles = {
            "100", "1000", "10000"
    };
    static JComboBox<String> numParticlesCombo = new JComboBox<>(numParticles);
    static int numberParticles;
    JButton closeButton = new JButton("OK");

    ParticleDialog() {
        numParticlesCombo.setSelectedIndex(0);
        closeButton.addActionListener(e -> {
            final Object selectedItem = numParticlesCombo.getSelectedItem();
            numberParticles = Integer.parseInt((String.valueOf(selectedItem)));
            dispose();
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.addToGridbag(this, new JLabel("Choose Number of Particles: "), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, numParticlesCombo, base_x, base_y + 1, 1, 1);
        GuiUtils.addToGridbag(this, closeButton, base_x, base_y + 2, 1, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }
}