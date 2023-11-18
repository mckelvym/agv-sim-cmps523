// Mark McKelvy
// CMPS 523
// Final Project
// File: AlgorithmControlPanel.java
package agv.sim.cmps523;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class AlgorithmControlPanel extends JDialog {
    static String[] bayesianFilter = {
            "None", "Extended Kalman Filter (EKF)", "Monte Carlo Localization (MCL)"
    };
    static JComboBox<String> bayesianFilterCombo = new JComboBox<>(bayesianFilter);
    JButton close = new JButton("OK");

    AlgorithmControlPanel() {
        bayesianFilterCombo.setSelectedIndex(0);
        bayesianFilterCombo.addActionListener(new AlgorithmChoiceHandler());
        close.addActionListener(e -> {
            if (!Objects.equals(bayesianFilterCombo.getSelectedItem(), "None"))
                dispose();
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.add_to_gridbag(this, new JLabel("Choose Bayesian Filter: "), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, bayesianFilterCombo, base_x, base_y + 1, 1, 1);
        GuiUtils.add_to_gridbag(this, close, base_x, base_y + 2, 1, 1);

        this.pack();
        GuiUtils.center_on_screen(this);
        this.setVisible(true);
    }


    private static class AlgorithmChoiceHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int choice = ((JComboBox<?>) e.getSource()).getSelectedIndex();
            if (choice > 0) {
                AGVsim.algorithm = choice;
                if (choice > 1)
                    new ParticleDialog();
            }
        }
    }
}
