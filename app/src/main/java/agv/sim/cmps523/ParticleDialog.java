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
    private static String[] numParticles = {
            "100", "1000", "10000"
    };
    private static JComboBox<String> numParticlesCombo = new JComboBox<>(getNumParticles());
    private static int numberParticles;
    private JButton closeButton = new JButton("OK");

    ParticleDialog() {
        getNumParticlesCombo().setSelectedIndex(0);
        getCloseButton().addActionListener(e -> {
            final Object selectedItem = getNumParticlesCombo().getSelectedItem();
            setNumberParticles(Integer.parseInt((String.valueOf(selectedItem))));
            dispose();
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.addToGridbag(this, new JLabel("Choose Number of Particles: "), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getNumParticlesCombo(), base_x, base_y + 1, 1, 1);
        GuiUtils.addToGridbag(this, getCloseButton(), base_x, base_y + 2, 1, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    public static String[] getNumParticles() {
        return numParticles;
    }

    public static void setNumParticles(String[] numParticles) {
        ParticleDialog.numParticles = numParticles;
    }

    public static JComboBox<String> getNumParticlesCombo() {
        return numParticlesCombo;
    }

    public static void setNumParticlesCombo(JComboBox<String> numParticlesCombo) {
        ParticleDialog.numParticlesCombo = numParticlesCombo;
    }

    public static int getNumberParticles() {
        return numberParticles;
    }

    public static void setNumberParticles(int numberParticles) {
        ParticleDialog.numberParticles = numberParticles;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(JButton closeButton) {
        this.closeButton = closeButton;
    }
}
