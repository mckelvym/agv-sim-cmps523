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
    private static final JSlider TESTBED_V_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
    private static final JSlider TESTBED_W_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 5, 2);
    private static final JSlider AGENT_A_1_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider AGENT_A_2_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider AGENT_A_3_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider AGENT_A_4_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider AGENT_A_5_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider AGENT_A_6_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider SENSOR_RANGE_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider SENSOR_BEARING_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final JSlider SENSOR_SIGNATURE_NOISE_SLIDER = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private JButton closeButton = new JButton("Close");


    NoiseControlPanel() {
        getTestbedVNoiseSlider().setMajorTickSpacing(2);
        getTestbedVNoiseSlider().setPaintTicks(true);
        getTestbedVNoiseSlider().setPaintLabels(true);

        getTestbedWNoiseSlider().setMajorTickSpacing(2);
        getTestbedWNoiseSlider().setPaintTicks(true);
        getTestbedWNoiseSlider().setPaintLabels(true);

        getAgentA1NoiseSlider().setMajorTickSpacing(5);
        getAgentA1NoiseSlider().setPaintTicks(true);
        getAgentA1NoiseSlider().setPaintLabels(true);

        getAgentA2NoiseSlider().setMajorTickSpacing(5);
        getAgentA2NoiseSlider().setPaintTicks(true);
        getAgentA2NoiseSlider().setPaintLabels(true);

        getAgentA3NoiseSlider().setMajorTickSpacing(5);
        getAgentA3NoiseSlider().setPaintTicks(true);
        getAgentA3NoiseSlider().setPaintLabels(true);

        getAgentA4NoiseSlider().setMajorTickSpacing(5);
        getAgentA4NoiseSlider().setPaintTicks(true);
        getAgentA4NoiseSlider().setPaintLabels(true);

        getAgentA5NoiseSlider().setMajorTickSpacing(5);
        getAgentA5NoiseSlider().setPaintTicks(true);
        getAgentA5NoiseSlider().setPaintLabels(true);

        getAgentA6NoiseSlider().setMajorTickSpacing(5);
        getAgentA6NoiseSlider().setPaintTicks(true);
        getAgentA6NoiseSlider().setPaintLabels(true);

        getSensorRangeNoiseSlider().setMajorTickSpacing(5);
        getSensorRangeNoiseSlider().setPaintTicks(true);
        getSensorRangeNoiseSlider().setPaintLabels(true);

        getSensorBearingNoiseSlider().setMajorTickSpacing(5);
        getSensorBearingNoiseSlider().setPaintTicks(true);
        getSensorBearingNoiseSlider().setPaintLabels(true);

        getSensorSignatureNoiseSlider().setMajorTickSpacing(5);
        getSensorSignatureNoiseSlider().setPaintTicks(true);
        getSensorSignatureNoiseSlider().setPaintLabels(true);

        getTestbedVNoiseSlider().addChangeListener(new TestbedVNoiseHandler());
        getTestbedWNoiseSlider().addChangeListener(new TestbedWNoiseHandler());
        getAgentA1NoiseSlider().addChangeListener(new AgentA1NoiseHandler());
        getAgentA2NoiseSlider().addChangeListener(new AgentA2NoiseHandler());
        getAgentA3NoiseSlider().addChangeListener(new AgentA3NoiseHandler());
        getAgentA4NoiseSlider().addChangeListener(new AgentA4NoiseHandler());
        getAgentA5NoiseSlider().addChangeListener(new AgentA5NoiseHandler());
        getAgentA6NoiseSlider().addChangeListener(new AgentA6NoiseHandler());
        getSensorRangeNoiseSlider().addChangeListener(new SensorRangeNoiseHandler());
        getSensorBearingNoiseSlider().addChangeListener(new SensorBearingNoiseHandler());
        getSensorSignatureNoiseSlider().addChangeListener(new SensorSignatureNoiseHandler());

        getCloseButton().addActionListener(e -> dispose());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.addToGridbag(this, new JLabel("Noise Configuration"), base_x, base_y, 2, 1);
        base_y += 2;
        GuiUtils.addToGridbag(this, new JLabel("Testbed Translational Noise (cm/sec):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getTestbedVNoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Testbed Rotational Noise (deg/sec):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getTestbedWNoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 1 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getAgentA1NoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 2 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getAgentA2NoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 3 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getAgentA3NoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Agent alpha 4 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getAgentA4NoiseSlider(), base_x + 1, base_y, 1, 1);
        if (AGVsim.getAlgorithm() == 2) {
            base_y++;
            GuiUtils.addToGridbag(this, new JLabel("Agent alpha 5 noise (%):"), base_x, base_y, 1, 1);
            GuiUtils.addToGridbag(this, getAgentA5NoiseSlider(), base_x + 1, base_y, 1, 1);
            base_y++;
            GuiUtils.addToGridbag(this, new JLabel("Agent alpha 6 noise (%):"), base_x, base_y, 1, 1);
            GuiUtils.addToGridbag(this, getAgentA6NoiseSlider(), base_x + 1, base_y, 1, 1);
        }
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sensor range noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorRangeNoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sensor bearing noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorBearingNoiseSlider(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Sensor signature noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getSensorSignatureNoiseSlider(), base_x + 1, base_y, 1, 1);

        base_y++;
        GuiUtils.addToGridbag(this, getCloseButton(), base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    public static double getTestbedVNoise() {
        return getValueDouble(getTestbedVNoiseSlider());
    }

    public static double getTestbedWNoise() {
        return Math.toRadians(getValueDouble(getTestbedWNoiseSlider()));
    }

    public static double getAlphaNoise(int i) {
        double scale = 100.0;
        return switch (i) {
            case 1 -> getValueDouble(getAgentA1NoiseSlider()) / scale;
            case 2 -> getValueDouble(getAgentA2NoiseSlider()) / scale;
            case 3 -> getValueDouble(getAgentA3NoiseSlider()) / scale;
            case 4 -> getValueDouble(getAgentA4NoiseSlider()) / scale;
            case 5 -> getValueDouble(getAgentA5NoiseSlider()) / scale;
            case 6 -> getValueDouble(getAgentA6NoiseSlider()) / scale;
            default -> -1.0;
        };
    }

    public static double getSensorNoise(int i) {
        double val = 0.0;

        switch (i) {
            case 1 -> {
                val = getValueDouble(getSensorRangeNoiseSlider());
                return val * val;
            }
            case 2 -> {
                val = getValueDouble(getSensorBearingNoiseSlider());
                return val * val;
            }
            case 3 -> {
                val = getValueDouble(getSensorSignatureNoiseSlider());
                return val * val;
            }
            default -> {
                return val;
            }
        }
    }

    public static JSlider getTestbedVNoiseSlider() {
        return TESTBED_V_NOISE_SLIDER;
    }

    public static JSlider getTestbedWNoiseSlider() {
        return TESTBED_W_NOISE_SLIDER;
    }

    public static JSlider getAgentA1NoiseSlider() {
        return AGENT_A_1_NOISE_SLIDER;
    }

    public static JSlider getAgentA2NoiseSlider() {
        return AGENT_A_2_NOISE_SLIDER;
    }

    public static JSlider getAgentA3NoiseSlider() {
        return AGENT_A_3_NOISE_SLIDER;
    }

    public static JSlider getAgentA4NoiseSlider() {
        return AGENT_A_4_NOISE_SLIDER;
    }

    public static JSlider getAgentA5NoiseSlider() {
        return AGENT_A_5_NOISE_SLIDER;
    }

    public static JSlider getAgentA6NoiseSlider() {
        return AGENT_A_6_NOISE_SLIDER;
    }

    public static JSlider getSensorRangeNoiseSlider() {
        return SENSOR_RANGE_NOISE_SLIDER;
    }

    public static JSlider getSensorBearingNoiseSlider() {
        return SENSOR_BEARING_NOISE_SLIDER;
    }

    public static JSlider getSensorSignatureNoiseSlider() {
        return SENSOR_SIGNATURE_NOISE_SLIDER;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(JButton closeButton) {
        this.closeButton = closeButton;
    }

    private static class TestbedVNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Testbed.setSigmaVNoise(getValueDouble(source));
            System.out.println("NoiseControlPanel: testbed translational velocity noise = " + source.getValue());
        }
    }

    private static class TestbedWNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Testbed.setSigmaWNoise(Math.toRadians(getValueDouble(source)));
            System.out.println("NoiseControlPanel: testbed rotational velocity noise = " + source.getValue());
        }
    }

    private static class AgentA1NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.getAgent().setA1Noise(getValueDouble(source) / 100.0);
            System.out.println("NoiseControlPanel: a1 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA2NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.getAgent().setA2Noise(getValueDouble(source) / 100.0);
            System.out.println("NoiseControlPanel: a2 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA3NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.getAgent().setA3Noise(getValueDouble(source) / 100.0);
            System.out.println("NoiseControlPanel: a3 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA4NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.getAgent().setA4Noise(getValueDouble(source) / 100.0);
            System.out.println("NoiseControlPanel: a4 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA5NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.getAgent().setA5Noise(getValueDouble(source) / 100.0);
            System.out.println("NoiseControlPanel: a5 noise = " + source.getValue() + "%");
        }
    }

    private static class AgentA6NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.getAgent().setA6Noise(getValueDouble(source) / 100.0);
            System.out.println("NoiseControlPanel: a6 noise = " + source.getValue() + "%");
        }
    }

    private static class SensorRangeNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = getValueDouble(source);
            AGVsim.getAgent().getQt().set(0, 0, val * val);
            System.out.println("NoiseControlPanel: sensor range noise sigma^2 = " + val);
        }
    }

    private static class SensorBearingNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = getValueDouble(source);
            AGVsim.getAgent().getQt().set(1, 1, val * val);
            System.out.println("NoiseControlPanel: sensor bearing noise sigma^2 = " + val);
        }
    }

    private static class SensorSignatureNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = getValueDouble(source);
            AGVsim.getAgent().getQt().set(2, 2, val * val);
            System.out.println("NoiseControlPanel: sensor signature noise sigma^2 = " + val);
        }
    }

}
