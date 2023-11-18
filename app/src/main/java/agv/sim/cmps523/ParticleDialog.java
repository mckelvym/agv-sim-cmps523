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
    static String[] nuparticles = {
            "100", "1000", "10000"
    };
    static JComboBox<String> nuparticles_combo = new JComboBox<>(nuparticles);
    static int number_particles;
    JButton close = new JButton("OK");

    ParticleDialog() {
        nuparticles_combo.setSelectedIndex(0);
        close.addActionListener(e -> {
            final Object selectedItem = nuparticles_combo.getSelectedItem();
            number_particles = Integer.parseInt((String.valueOf(selectedItem)));
            dispose();
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.add_to_gridbag(this, new JLabel("Choose Number of Particles: "), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, nuparticles_combo, base_x, base_y + 1, 1, 1);
        GuiUtils.add_to_gridbag(this, close, base_x, base_y + 2, 1, 1);

        this.pack();
        GuiUtils.center_on_screen(this);
        this.setVisible(true);
    }
}