// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorControlPanel.java
package agv.sim.cmps523;

import static agv.sim.cmps523.GuiUtils.getJComboBoxSelectedItem;
import static agv.sim.cmps523.GuiUtils.getValueDouble;
import static java.lang.System.out;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SensorControlPanel extends JDialog {
    static final JSlider Z_HIT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider Z_SHORT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider Z_MAX_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider Z_RAND_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider SIGMA_HIT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider LAMBDA_SHORT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static String[] sensorMaxRange = {
            "200", "300", "500", "1000"
    };
    static JComboBox<String> sensorMaxRangeCombo = new JComboBox<>(sensorMaxRange);
    static String[] sensorAngRes = {
            "5", "2", "1"
    };
    static JComboBox<String> sensorAngResCombo = new JComboBox<>(sensorAngRes);
    static String[] sensorDisplay = {
            "Yes", "No"
    };
    static JComboBox<String> sensorDisplayCombo = new JComboBox<>(sensorDisplay);
    static String[] sensorReturnDisplay = {
            "Yes", "No"
    };
    static JComboBox<String> sensorReturnDisplayCombo = new JComboBox<>(sensorReturnDisplay);
    JButton closeButton = new JButton("Close");

    SensorControlPanel() {
        sensorMaxRangeCombo.setSelectedIndex(2);
        sensorMaxRangeCombo.addActionListener(new SensorMaxRangeHandler());
        sensorAngResCombo.setSelectedIndex(0);
        sensorAngResCombo.addActionListener(new SensorAngResHandler());
        sensorDisplayCombo.addActionListener(new SensorDisplayHandler());
        sensorReturnDisplayCombo.addActionListener(new SensorReturnDisplayHandler());

        Z_HIT_SLIDER.setMajorTickSpacing(5);
        Z_HIT_SLIDER.setPaintTicks(true);
        Z_HIT_SLIDER.setPaintLabels(true);

        Z_SHORT_SLIDER.setMajorTickSpacing(5);
        Z_SHORT_SLIDER.setPaintTicks(true);
        Z_SHORT_SLIDER.setPaintLabels(true);

        Z_MAX_SLIDER.setMajorTickSpacing(5);
        Z_MAX_SLIDER.setPaintTicks(true);
        Z_MAX_SLIDER.setPaintLabels(true);

        Z_RAND_SLIDER.setMajorTickSpacing(5);
        Z_RAND_SLIDER.setPaintTicks(true);
        Z_RAND_SLIDER.setPaintLabels(true);

        SIGMA_HIT_SLIDER.setMajorTickSpacing(5);
        SIGMA_HIT_SLIDER.setPaintTicks(true);
        SIGMA_HIT_SLIDER.setPaintLabels(true);

        LAMBDA_SHORT_SLIDER.setMajorTickSpacing(10);
        LAMBDA_SHORT_SLIDER.setPaintTicks(true);
        LAMBDA_SHORT_SLIDER.setPaintLabels(true);

        Z_HIT_SLIDER.addChangeListener(new ZHitHandler());
        Z_SHORT_SLIDER.addChangeListener(new ZShortHandler());
        Z_MAX_SLIDER.addChangeListener(new ZMaxHandler());
        Z_RAND_SLIDER.addChangeListener(new ZRandHandler());
        SIGMA_HIT_SLIDER.addChangeListener(new SigmaHitHandler());
        LAMBDA_SHORT_SLIDER.addChangeListener(new LambdaShortHandler());

        Hashtable<Integer, JLabel> z_hit_labels = new Hashtable<>();
        z_hit_labels.put((0), new JLabel("None"));
        z_hit_labels.put((50), new JLabel("Less"));
        z_hit_labels.put((100), new JLabel("More"));
        Z_HIT_SLIDER.setLabelTable(z_hit_labels);

        Hashtable<Integer, JLabel> z_short_labels = new Hashtable<>();
        z_short_labels.put((0), new JLabel("None"));
        z_short_labels.put((50), new JLabel("Less"));
        z_short_labels.put((100), new JLabel("More"));
        Z_SHORT_SLIDER.setLabelTable(z_short_labels);

        Hashtable<Integer, JLabel> z_max_labels = new Hashtable<>();
        z_max_labels.put((0), new JLabel("None"));
        z_max_labels.put((50), new JLabel("Less"));
        z_max_labels.put((100), new JLabel("More"));
        Z_MAX_SLIDER.setLabelTable(z_max_labels);

        Hashtable<Integer, JLabel> z_rand_labels = new Hashtable<>();
        z_rand_labels.put((0), new JLabel("None"));
        z_rand_labels.put((50), new JLabel("Less"));
        z_rand_labels.put((100), new JLabel("More"));
        Z_RAND_SLIDER.setLabelTable(z_rand_labels);


        closeButton.addActionListener(e -> dispose());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.add_to_gridbag(this, new JLabel("Sensor Configuration"), base_x, base_y, 2, 1);
        base_y += 2;
        GuiUtils.add_to_gridbag(this, new JLabel("Angular Resolution:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensorAngResCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Max Range:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensorMaxRangeCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Display Beams:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensorDisplayCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Display Returned Beams:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensorReturnDisplayCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_hit:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, Z_HIT_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_short:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, Z_SHORT_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_max:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, Z_MAX_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_rand:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, Z_RAND_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Sigma_hit:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, SIGMA_HIT_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Lambda_short:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, LAMBDA_SHORT_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, closeButton, base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.center_on_screen(this);
        this.setVisible(true);
    }

    // Algorithm bearange_finder_model page 158
    public static double get_sensor_noise_probability(int i) {
        return switch (i) {
            case 0 -> getValueDouble(Z_HIT_SLIDER);
            case 1 -> getValueDouble(Z_SHORT_SLIDER);
            case 2 -> getValueDouble(Z_MAX_SLIDER);
            case 3 -> getValueDouble(Z_RAND_SLIDER);
            default -> 0.0;
        };
    }

    public static double get_sigma_hit() {
        return getValueDouble(SIGMA_HIT_SLIDER);
    }

    public static double get_lambda_short() {
        return getValueDouble(LAMBDA_SHORT_SLIDER);
    }

    private static class SensorMaxRangeHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String range = getJComboBoxSelectedItem(e);
            AGVsim.agent.sensor.set_max_range(Double.parseDouble(range));
            System.out.println("sensor range=" + range);
        }
    }

    private static class SensorAngResHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String res = getJComboBoxSelectedItem(e);
            AGVsim.agent.sensor.set_angular_resolution(Double.parseDouble(res));
            System.out.println("sensor angular resolution=" + res);
        }
    }

    private static class SensorDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = getJComboBoxSelectedItem(e);
            AGVsim.testbedview.drawSensorBeams = disp.equals("Yes");
            AGVsim.testbedview.repaint();
        }
    }

    private static class SensorReturnDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = getJComboBoxSelectedItem(e);
            AGVsim.testbedview.drawSensorReturnBeams = disp.equals("Yes");
            AGVsim.testbedview.repaint();
        }
    }

    private static class ZHitHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.agent.sensor.normalize_noise();
        }
    }

    private static class ZShortHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.agent.sensor.normalize_noise();
        }
    }

    private static class ZMaxHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.agent.sensor.normalize_noise();
        }
    }

    private static class ZRandHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.agent.sensor.normalize_noise();
        }
    }

    private static class SigmaHitHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            final double valueDouble = getValueDouble(source);
            AGVsim.agent.sensor.sigmaHit = valueDouble;
            out.println("SIGMA_hit = " + valueDouble);
        }
    }

    private static class LambdaShortHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            final double valueDouble = getValueDouble(source);
            AGVsim.agent.sensor.lambdaShort = valueDouble;
            out.println("LAMBDA_short = " + valueDouble);
        }
    }
}
