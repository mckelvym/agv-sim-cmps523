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
    private static final JSlider Z_HIT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    private static final JSlider Z_SHORT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    private static final JSlider Z_MAX_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    private static final JSlider Z_RAND_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    private static final JSlider SIGMA_HIT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider LAMBDA_SHORT_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static String[] sensorMaxRange = {
            "200", "300", "500", "1000"
    };
    private static JComboBox<String> sensorMaxRangeCombo = new JComboBox<>(getSensorMaxRange());
    private static String[] sensorAngRes = {
            "5", "2", "1"
    };
    private static JComboBox<String> sensorAngResCombo = new JComboBox<>(getSensorAngRes());
    private static String[] sensorDisplay = {
            "Yes", "No"
    };
    private static JComboBox<String> sensorDisplayCombo = new JComboBox<>(getSensorDisplay());
    private static String[] sensorReturnDisplay = {
            "Yes", "No"
    };
    private static JComboBox<String> sensorReturnDisplayCombo = new JComboBox<>(getSensorReturnDisplay());
    private JButton closeButton = new JButton("Close");

    SensorControlPanel() {
        getSensorMaxRangeCombo().setSelectedIndex(2);
        getSensorMaxRangeCombo().addActionListener(new SensorMaxRangeHandler());
        getSensorAngResCombo().setSelectedIndex(0);
        getSensorAngResCombo().addActionListener(new SensorAngResHandler());
        getSensorDisplayCombo().addActionListener(new SensorDisplayHandler());
        getSensorReturnDisplayCombo().addActionListener(new SensorReturnDisplayHandler());

        getzHitSlider().setMajorTickSpacing(5);
        getzHitSlider().setPaintTicks(true);
        getzHitSlider().setPaintLabels(true);

        getzShortSlider().setMajorTickSpacing(5);
        getzShortSlider().setPaintTicks(true);
        getzShortSlider().setPaintLabels(true);

        getzMaxSlider().setMajorTickSpacing(5);
        getzMaxSlider().setPaintTicks(true);
        getzMaxSlider().setPaintLabels(true);

        getzRandSlider().setMajorTickSpacing(5);
        getzRandSlider().setPaintTicks(true);
        getzRandSlider().setPaintLabels(true);

        getSigmaHitSlider().setMajorTickSpacing(5);
        getSigmaHitSlider().setPaintTicks(true);
        getSigmaHitSlider().setPaintLabels(true);

        getLambdaShortSlider().setMajorTickSpacing(10);
        getLambdaShortSlider().setPaintTicks(true);
        getLambdaShortSlider().setPaintLabels(true);

        getzHitSlider().addChangeListener(new ZHitHandler());
        getzShortSlider().addChangeListener(new ZShortHandler());
        getzMaxSlider().addChangeListener(new ZMaxHandler());
        getzRandSlider().addChangeListener(new ZRandHandler());
        getSigmaHitSlider().addChangeListener(new SigmaHitHandler());
        getLambdaShortSlider().addChangeListener(new LambdaShortHandler());

        Hashtable<Integer, JLabel> z_hit_labels = new Hashtable<>();
        z_hit_labels.put((0), new JLabel("None"));
        z_hit_labels.put((50), new JLabel("Less"));
        z_hit_labels.put((100), new JLabel("More"));
        getzHitSlider().setLabelTable(z_hit_labels);

        Hashtable<Integer, JLabel> z_short_labels = new Hashtable<>();
        z_short_labels.put((0), new JLabel("None"));
        z_short_labels.put((50), new JLabel("Less"));
        z_short_labels.put((100), new JLabel("More"));
        getzShortSlider().setLabelTable(z_short_labels);

        Hashtable<Integer, JLabel> z_max_labels = new Hashtable<>();
        z_max_labels.put((0), new JLabel("None"));
        z_max_labels.put((50), new JLabel("Less"));
        z_max_labels.put((100), new JLabel("More"));
        getzMaxSlider().setLabelTable(z_max_labels);

        Hashtable<Integer, JLabel> z_rand_labels = new Hashtable<>();
        z_rand_labels.put((0), new JLabel("None"));
        z_rand_labels.put((50), new JLabel("Less"));
        z_rand_labels.put((100), new JLabel("More"));
        getzRandSlider().setLabelTable(z_rand_labels);


        getCloseButton().addActionListener(e -> dispose());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.addToGridbag(this, new JLabel("Sensor Configuration"), base_x, base_y, 2, 1);
        base_y += 2;
        GuiUtils.addToGridbag(this, new JLabel("Angular Resolution:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorAngResCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Max Range:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorMaxRangeCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Display Beams:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorDisplayCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Display Returned Beams:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorReturnDisplayCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Z_hit:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getzHitSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Z_short:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getzShortSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Z_max:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getzMaxSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Z_rand:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getzRandSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sigma_hit:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSigmaHitSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Lambda_short:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getLambdaShortSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, getCloseButton(), base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    // Algorithm bearange_finder_model page 158
    public static double getSensorNoiseProbability(int i) {
        return switch (i) {
            case 0 -> getValueDouble(getzHitSlider());
            case 1 -> getValueDouble(getzShortSlider());
            case 2 -> getValueDouble(getzMaxSlider());
            case 3 -> getValueDouble(getzRandSlider());
            default -> 0.0;
        };
    }

    public static double getSigmaHit() {
        return getValueDouble(getSigmaHitSlider());
    }

    public static double getLambdaShort() {
        return getValueDouble(getLambdaShortSlider());
    }

    public static JSlider getzHitSlider() {
        return Z_HIT_SLIDER;
    }

    public static JSlider getzShortSlider() {
        return Z_SHORT_SLIDER;
    }

    public static JSlider getzMaxSlider() {
        return Z_MAX_SLIDER;
    }

    public static JSlider getzRandSlider() {
        return Z_RAND_SLIDER;
    }

    public static JSlider getSigmaHitSlider() {
        return SIGMA_HIT_SLIDER;
    }

    public static JSlider getLambdaShortSlider() {
        return LAMBDA_SHORT_SLIDER;
    }

    public static String[] getSensorMaxRange() {
        return sensorMaxRange;
    }

    public static void setSensorMaxRange(String[] sensorMaxRange) {
        SensorControlPanel.sensorMaxRange = sensorMaxRange;
    }

    public static JComboBox<String> getSensorMaxRangeCombo() {
        return sensorMaxRangeCombo;
    }

    public static void setSensorMaxRangeCombo(JComboBox<String> sensorMaxRangeCombo) {
        SensorControlPanel.sensorMaxRangeCombo = sensorMaxRangeCombo;
    }

    public static String[] getSensorAngRes() {
        return sensorAngRes;
    }

    public static void setSensorAngRes(String[] sensorAngRes) {
        SensorControlPanel.sensorAngRes = sensorAngRes;
    }

    public static JComboBox<String> getSensorAngResCombo() {
        return sensorAngResCombo;
    }

    public static void setSensorAngResCombo(JComboBox<String> sensorAngResCombo) {
        SensorControlPanel.sensorAngResCombo = sensorAngResCombo;
    }

    public static String[] getSensorDisplay() {
        return sensorDisplay;
    }

    public static void setSensorDisplay(String[] sensorDisplay) {
        SensorControlPanel.sensorDisplay = sensorDisplay;
    }

    public static JComboBox<String> getSensorDisplayCombo() {
        return sensorDisplayCombo;
    }

    public static void setSensorDisplayCombo(JComboBox<String> sensorDisplayCombo) {
        SensorControlPanel.sensorDisplayCombo = sensorDisplayCombo;
    }

    public static String[] getSensorReturnDisplay() {
        return sensorReturnDisplay;
    }

    public static void setSensorReturnDisplay(String[] sensorReturnDisplay) {
        SensorControlPanel.sensorReturnDisplay = sensorReturnDisplay;
    }

    public static JComboBox<String> getSensorReturnDisplayCombo() {
        return sensorReturnDisplayCombo;
    }

    public static void setSensorReturnDisplayCombo(JComboBox<String> sensorReturnDisplayCombo) {
        SensorControlPanel.sensorReturnDisplayCombo = sensorReturnDisplayCombo;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(JButton closeButton) {
        this.closeButton = closeButton;
    }

    private static class SensorMaxRangeHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String range = getJComboBoxSelectedItem(e);
            AGVsim.getAgent().getSensor().set_max_range(Double.parseDouble(range));
            System.out.println("sensor range=" + range);
        }
    }

    private static class SensorAngResHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String res = getJComboBoxSelectedItem(e);
            AGVsim.getAgent().getSensor().set_angular_resolution(Double.parseDouble(res));
            System.out.println("sensor angular resolution=" + res);
        }
    }

    private static class SensorDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = getJComboBoxSelectedItem(e);
            AGVsim.getTestbedview().setDrawSensorBeams(disp.equals("Yes"));
            AGVsim.getTestbedview().repaint();
        }
    }

    private static class SensorReturnDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = getJComboBoxSelectedItem(e);
            AGVsim.getTestbedview().setDrawSensorReturnBeams(disp.equals("Yes"));
            AGVsim.getTestbedview().repaint();
        }
    }

    private static class ZHitHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.getAgent().getSensor().normalize_noise();
        }
    }

    private static class ZShortHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.getAgent().getSensor().normalize_noise();
        }
    }

    private static class ZMaxHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.getAgent().getSensor().normalize_noise();
        }
    }

    private static class ZRandHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.getAgent().getSensor().normalize_noise();
        }
    }

    private static class SigmaHitHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            final double valueDouble = getValueDouble(source);
            AGVsim.getAgent().getSensor().sigmaHit = valueDouble;
            out.println("SIGMA_hit = " + valueDouble);
        }
    }

    private static class LambdaShortHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            final double valueDouble = getValueDouble(source);
            AGVsim.getAgent().getSensor().lambdaShort = valueDouble;
            out.println("LAMBDA_short = " + valueDouble);
        }
    }
}
