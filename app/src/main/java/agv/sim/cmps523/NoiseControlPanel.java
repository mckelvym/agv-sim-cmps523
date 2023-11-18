// Mark McKelvy
// CMPS 523
// Final Project
// File: NoiseControlPanel.java
package agv.sim.cmps523;

import static agv.sim.cmps523.GuiUtils.getValueDouble;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NoiseControlPanel extends JDialog {
    static final JSlider TESTBED_V_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
    static final JSlider TESTBED_W_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 5, 2);
    static final JSlider AGENT_A_1_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider AGENT_A_2_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider AGENT_A_3_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider AGENT_A_4_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider AGENT_A_5_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider AGENT_A_6_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider SENSOR_RANGE_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider SENSOR_BEARING_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider SENSOR_SIGNATURE_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    JButton closeButton = new JButton("Close");


    NoiseControlPanel() {
        TESTBED_V_NOISE_SLIDER.setMajorTickSpacing(2);
        TESTBED_V_NOISE_SLIDER.setPaintTicks(true);
        TESTBED_V_NOISE_SLIDER.setPaintLabels(true);

        TESTBED_W_NOISE_SLIDER.setMajorTickSpacing(2);
        TESTBED_W_NOISE_SLIDER.setPaintTicks(true);
        TESTBED_W_NOISE_SLIDER.setPaintLabels(true);

        AGENT_A_1_NOISE_SLIDER.setMajorTickSpacing(5);
        AGENT_A_1_NOISE_SLIDER.setPaintTicks(true);
        AGENT_A_1_NOISE_SLIDER.setPaintLabels(true);

        AGENT_A_2_NOISE_SLIDER.setMajorTickSpacing(5);
        AGENT_A_2_NOISE_SLIDER.setPaintTicks(true);
        AGENT_A_2_NOISE_SLIDER.setPaintLabels(true);

        AGENT_A_3_NOISE_SLIDER.setMajorTickSpacing(5);
        AGENT_A_3_NOISE_SLIDER.setPaintTicks(true);
        AGENT_A_3_NOISE_SLIDER.setPaintLabels(true);

        AGENT_A_4_NOISE_SLIDER.setMajorTickSpacing(5);
        AGENT_A_4_NOISE_SLIDER.setPaintTicks(true);
        AGENT_A_4_NOISE_SLIDER.setPaintLabels(true);

        AGENT_A_5_NOISE_SLIDER.setMajorTickSpacing(5);
        AGENT_A_5_NOISE_SLIDER.setPaintTicks(true);
        AGENT_A_5_NOISE_SLIDER.setPaintLabels(true);

        AGENT_A_6_NOISE_SLIDER.setMajorTickSpacing(5);
        AGENT_A_6_NOISE_SLIDER.setPaintTicks(true);
        AGENT_A_6_NOISE_SLIDER.setPaintLabels(true);

        SENSOR_RANGE_NOISE_SLIDER.setMajorTickSpacing(5);
        SENSOR_RANGE_NOISE_SLIDER.setPaintTicks(true);
        SENSOR_RANGE_NOISE_SLIDER.setPaintLabels(true);

        SENSOR_BEARING_NOISE_SLIDER.setMajorTickSpacing(5);
        SENSOR_BEARING_NOISE_SLIDER.setPaintTicks(true);
        SENSOR_BEARING_NOISE_SLIDER.setPaintLabels(true);

        SENSOR_SIGNATURE_NOISE_SLIDER.setMajorTickSpacing(5);
        SENSOR_SIGNATURE_NOISE_SLIDER.setPaintTicks(true);
        SENSOR_SIGNATURE_NOISE_SLIDER.setPaintLabels(true);

        TESTBED_V_NOISE_SLIDER.addChangeListener(new TestbedVNoiseHandler());
        TESTBED_W_NOISE_SLIDER.addChangeListener(new TestbedWNoiseHandler());
        AGENT_A_1_NOISE_SLIDER.addChangeListener(new AgentA1NoiseHandler());
        AGENT_A_2_NOISE_SLIDER.addChangeListener(new AgentA2NoiseHandler());
        AGENT_A_3_NOISE_SLIDER.addChangeListener(new AgentA3NoiseHandler());
        AGENT_A_4_NOISE_SLIDER.addChangeListener(new AgentA4NoiseHandler());
        AGENT_A_5_NOISE_SLIDER.addChangeListener(new AgentA5NoiseHandler());
        AGENT_A_6_NOISE_SLIDER.addChangeListener(new AgentA6NoiseHandler());
        SENSOR_RANGE_NOISE_SLIDER.addChangeListener(new SensorRangeNoiseHandler());
        SENSOR_BEARING_NOISE_SLIDER.addChangeListener(new SensorBearingNoiseHandler());
        SENSOR_SIGNATURE_NOISE_SLIDER.addChangeListener(new SensorSignatureNoiseHandler());

        closeButton.addActionListener(e -> dispose());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.addToGridbag(this, new JLabel("Noise Configuration"), base_x, base_y, 2, 1);
        base_y += 2;
        GuiUtils.addToGridbag(this, new JLabel("Testbed Translational Noise (cm/sec):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, TESTBED_V_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Testbed Rotational Noise (deg/sec):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, TESTBED_W_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 1 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, AGENT_A_1_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 2 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, AGENT_A_2_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 3 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, AGENT_A_3_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 4 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, AGENT_A_4_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        if (AGVsim.algorithm == 2) {
            base_y++;
            GuiUtils.addToGridbag(this, new JLabel("Agent alpha 5 noise (%):"), base_x, base_y, 1, 1);
            GuiUtils.addToGridbag(this, AGENT_A_5_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
            base_y++;
            GuiUtils.addToGridbag(this, new JLabel("Agent alpha 6 noise (%):"), base_x, base_y, 1, 1);
            GuiUtils.addToGridbag(this, AGENT_A_6_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        }
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sensor range noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, SENSOR_RANGE_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sensor bearing noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, SENSOR_BEARING_NOISE_SLIDER, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sensor signature noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, SENSOR_SIGNATURE_NOISE_SLIDER, base_x + 1, base_y, 1, 1);

        base_y++;
        GuiUtils.addToGridbag(this, closeButton, base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    public static double getTestbedVNoise() {
        return getValueDouble(TESTBED_V_NOISE_SLIDER);
    }

    public static double getTestbedWNoise() {
        return Math.toRadians(getValueDouble(TESTBED_W_NOISE_SLIDER));
    }

    public static double getAlphaNoise(int i) {
        double scale = 100.0;
        return switch (i) {
            case 1 -> getValueDouble(AGENT_A_1_NOISE_SLIDER) / scale;
            case 2 -> getValueDouble(AGENT_A_2_NOISE_SLIDER) / scale;
            case 3 -> getValueDouble(AGENT_A_3_NOISE_SLIDER) / scale;
            case 4 -> getValueDouble(AGENT_A_4_NOISE_SLIDER) / scale;
            case 5 -> getValueDouble(AGENT_A_5_NOISE_SLIDER) / scale;
            case 6 -> getValueDouble(AGENT_A_6_NOISE_SLIDER) / scale;
            default -> -1.0;
        };
    }

    public static double getSensorNoise(int i) {
        double val = 0.0;

        switch (i) {
            case 1 -> {
                val = getValueDouble(SENSOR_RANGE_NOISE_SLIDER);
                return val * val;
            }
            case 2 -> {
                val = getValueDouble(SENSOR_BEARING_NOISE_SLIDER);
                return val * val;
            }
            case 3 -> {
                val = getValueDouble(SENSOR_SIGNATURE_NOISE_SLIDER);
                return val * val;
            }
            default -> {
                return val;
            }
        }
    }

    private static class TestbedVNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Testbed.sigmaVNoise = getValueDouble(source);
            System.out.println("NoiseControlPanel: testbed translational velocity noise = " + source.getValue());
        }
    }

    private static class TestbedWNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Testbed.sigmaWNoise = Math.toRadians(getValueDouble(source));
            System.out.println("NoiseControlPanel: testbed rotational velocity noise = " + source.getValue());
        }
    }

    private static class AgentA1NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.agent.a1Noise = getValueDouble(source) / 100.0;
            System.out.println("NoiseControlPanel: a1 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA2NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.agent.a2Noise = getValueDouble(source) / 100.0;
            System.out.println("NoiseControlPanel: a2 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA3NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.agent.a3Noise = getValueDouble(source) / 100.0;
            System.out.println("NoiseControlPanel: a3 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA4NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.agent.a4Noise = getValueDouble(source) / 100.0;
            System.out.println("NoiseControlPanel: a4 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA5NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.agent.a5Noise = getValueDouble(source) / 100.0;
            System.out.println("NoiseControlPanel: a5 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA6NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.agent.a6Noise = getValueDouble(source) / 100.0;
            System.out.println("NoiseControlPanel: a6 noise = " + source.getValue() + "%");
        }
    }

    private static class SensorRangeNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = getValueDouble(source);
            AGVsim.agent.Qt.set(0, 0, val * val);
            System.out.println("NoiseControlPanel: sensor range noise sigma^2 = " + val);
        }
    }

    private static class SensorBearingNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = getValueDouble(source);
            AGVsim.agent.Qt.set(1, 1, val * val);
            System.out.println("NoiseControlPanel: sensor bearing noise sigma^2 = " + val);
        }
    }

    private static class SensorSignatureNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = getValueDouble(source);
            AGVsim.agent.Qt.set(2, 2, val * val);
            System.out.println("NoiseControlPanel: sensor signature noise sigma^2 = " + val);
        }
    }

}
