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
    private static String[] bayesianFilter = {
            "None", "Extended Kalman Filter (EKF)", "Monte Carlo Localization (MCL)"
    };
    private static JComboBox<String> bayesianFilterCombo = new JComboBox<>(getBayesianFilter());
    private JButton close = new JButton("OK");

    AlgorithmControlPanel() {
        getBayesianFilterCombo().setSelectedIndex(0);
        getBayesianFilterCombo().addActionListener(new AlgorithmChoiceHandler());
        getClose().addActionListener(e -> {
            if (!Objects.equals(getBayesianFilterCombo().getSelectedItem(), "None"))
                dispose();
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.addToGridbag(this, new JLabel("Choose Bayesian Filter: "), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getBayesianFilterCombo(), base_x, base_y + 1, 1, 1);
        GuiUtils.addToGridbag(this, getClose(), base_x, base_y + 2, 1, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    public static String[] getBayesianFilter() {
        return bayesianFilter;
    }

    public static void setBayesianFilter(String[] bayesianFilter) {
        AlgorithmControlPanel.bayesianFilter = bayesianFilter;
    }

    public static JComboBox<String> getBayesianFilterCombo() {
        return bayesianFilterCombo;
    }

    public static void setBayesianFilterCombo(JComboBox<String> bayesianFilterCombo) {
        AlgorithmControlPanel.bayesianFilterCombo = bayesianFilterCombo;
    }

    public JButton getClose() {
        return close;
    }

    public void setClose(JButton close) {
        this.close = close;
    }


    private static class AlgorithmChoiceHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int choice = ((JComboBox<?>) e.getSource()).getSelectedIndex();
            if (choice > 0) {
                AGVsim.setAlgorithm(choice);
                if (choice > 1)
                    new ParticleDialog();
            }
        }
    }
}
