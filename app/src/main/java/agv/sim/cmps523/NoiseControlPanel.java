// Mark McKelvy
// CMPS 523
// Final Project
// File: NoiseControlPanel.java
package agv.sim.cmps523;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NoiseControlPanel extends JDialog {
    static final JSlider testbed_v_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
    static final JSlider testbed_w_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 5, 2);
    static final JSlider agent_a1_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider agent_a2_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider agent_a3_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider agent_a4_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider agent_a5_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider agent_a6_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider sensor_range_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider sensor_bearing_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider sensor_signature_noise_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final long serialVersionUID = 1L;
    JButton close = new JButton("Close");


    NoiseControlPanel() {
        testbed_v_noise_slider.setMajorTickSpacing(2);
        testbed_v_noise_slider.setPaintTicks(true);
        testbed_v_noise_slider.setPaintLabels(true);

        testbed_w_noise_slider.setMajorTickSpacing(2);
        testbed_w_noise_slider.setPaintTicks(true);
        testbed_w_noise_slider.setPaintLabels(true);

        agent_a1_noise_slider.setMajorTickSpacing(5);
        agent_a1_noise_slider.setPaintTicks(true);
        agent_a1_noise_slider.setPaintLabels(true);

        agent_a2_noise_slider.setMajorTickSpacing(5);
        agent_a2_noise_slider.setPaintTicks(true);
        agent_a2_noise_slider.setPaintLabels(true);

        agent_a3_noise_slider.setMajorTickSpacing(5);
        agent_a3_noise_slider.setPaintTicks(true);
        agent_a3_noise_slider.setPaintLabels(true);

        agent_a4_noise_slider.setMajorTickSpacing(5);
        agent_a4_noise_slider.setPaintTicks(true);
        agent_a4_noise_slider.setPaintLabels(true);

        agent_a5_noise_slider.setMajorTickSpacing(5);
        agent_a5_noise_slider.setPaintTicks(true);
        agent_a5_noise_slider.setPaintLabels(true);

        agent_a6_noise_slider.setMajorTickSpacing(5);
        agent_a6_noise_slider.setPaintTicks(true);
        agent_a6_noise_slider.setPaintLabels(true);

        sensor_range_noise_slider.setMajorTickSpacing(5);
        sensor_range_noise_slider.setPaintTicks(true);
        sensor_range_noise_slider.setPaintLabels(true);

        sensor_bearing_noise_slider.setMajorTickSpacing(5);
        sensor_bearing_noise_slider.setPaintTicks(true);
        sensor_bearing_noise_slider.setPaintLabels(true);

        sensor_signature_noise_slider.setMajorTickSpacing(5);
        sensor_signature_noise_slider.setPaintTicks(true);
        sensor_signature_noise_slider.setPaintLabels(true);

        testbed_v_noise_slider.addChangeListener(new TestbedVNoiseHandler());
        testbed_w_noise_slider.addChangeListener(new TestbedWNoiseHandler());
        agent_a1_noise_slider.addChangeListener(new AgentA1NoiseHandler());
        agent_a2_noise_slider.addChangeListener(new AgentA2NoiseHandler());
        agent_a3_noise_slider.addChangeListener(new AgentA3NoiseHandler());
        agent_a4_noise_slider.addChangeListener(new AgentA4NoiseHandler());
        agent_a5_noise_slider.addChangeListener(new AgentA5NoiseHandler());
        agent_a6_noise_slider.addChangeListener(new AgentA6NoiseHandler());
        sensor_range_noise_slider.addChangeListener(new SensorRangeNoiseHandler());
        sensor_bearing_noise_slider.addChangeListener(new SensorBearingNoiseHandler());
        sensor_signature_noise_slider.addChangeListener(new SensorSignatureNoiseHandler());

        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.add_to_gridbag(this, new JLabel("Noise Configuration"), base_x, base_y, 2, 1);
        base_y += 2;
        GuiUtils.add_to_gridbag(this, new JLabel("Testbed Translational Noise (cm/sec):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, testbed_v_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Testbed Rotational Noise (deg/sec):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, testbed_w_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Agent alpha 1 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, agent_a1_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Agent alpha 2 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, agent_a2_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Agent alpha 3 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, agent_a3_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Agent alpha 4 noise (%):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, agent_a4_noise_slider, base_x + 1, base_y, 1, 1);
        if (AGVsim.algorithm == 2) {
            base_y++;
            GuiUtils.add_to_gridbag(this, new JLabel("Agent alpha 5 noise (%):"), base_x, base_y, 1, 1);
            GuiUtils.add_to_gridbag(this, agent_a5_noise_slider, base_x + 1, base_y, 1, 1);
            base_y++;
            GuiUtils.add_to_gridbag(this, new JLabel("Agent alpha 6 noise (%):"), base_x, base_y, 1, 1);
            GuiUtils.add_to_gridbag(this, agent_a6_noise_slider, base_x + 1, base_y, 1, 1);
        }
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Sensor range noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_range_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Sensor bearing noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_bearing_noise_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Sensor signature noise sigma:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_signature_noise_slider, base_x + 1, base_y, 1, 1);

        base_y++;
        GuiUtils.add_to_gridbag(this, close, base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.center_on_screen(this);
        this.setVisible(true);
    }

    public static double get_testbed_v_noise() {
        return new Double(testbed_v_noise_slider.getValue()).doubleValue();
    }

    public static double get_testbed_w_noise() {
        return Math.toRadians(new Double(testbed_w_noise_slider.getValue()).doubleValue());
    }

    public static double get_alpha_noise(int i) {
        double scale = 100.0;
        switch (i) {
            case 1:
                return (new Double(agent_a1_noise_slider.getValue()).doubleValue()) / scale;
            case 2:
                return (new Double(agent_a2_noise_slider.getValue()).doubleValue()) / scale;
            case 3:
                return (new Double(agent_a3_noise_slider.getValue()).doubleValue()) / scale;
            case 4:
                return (new Double(agent_a4_noise_slider.getValue()).doubleValue()) / scale;
            case 5:
                return (new Double(agent_a5_noise_slider.getValue()).doubleValue()) / scale;
            case 6:
                return (new Double(agent_a6_noise_slider.getValue()).doubleValue()) / scale;
            default:
                return -1.0;
        }
    }

    public static double get_sensor_noise(int i) {
        double val = 0.0;

        switch (i) {
            case 1:
                val = (new Double(sensor_range_noise_slider.getValue()).doubleValue());
                return val * val;
            case 2:
                val = (new Double(sensor_bearing_noise_slider.getValue()).doubleValue());
                return val * val;
            case 3:
                val = (new Double(sensor_signature_noise_slider.getValue()).doubleValue());
                return val * val;
            default:
                return val;
        }
    }

    private class TestbedVNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Testbed.m_sigma_v_noise = (new Double(source.getValue()).doubleValue());
            System.out.println("NoiseControlPanel: testbed translational velocity noise = " + source.getValue());
        }
    }

    private class TestbedWNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Testbed.m_sigma_w_noise = Math.toRadians((new Double(source.getValue()).doubleValue()));
            System.out.println("NoiseControlPanel: testbed rotational velocity noise = " + source.getValue());
        }
    }

    private class AgentA1NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.m_agent.m_a1_noise = (new Double(source.getValue()).doubleValue()) / 100.0;
            System.out.println("NoiseControlPanel: a1 noise = " + source.getValue() + "%");
        }
    }

    private class AgentA2NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.m_agent.m_a2_noise = (new Double(source.getValue()).doubleValue()) / 100.0;
            System.out.println("NoiseControlPanel: a2 noise = " + source.getValue() + "%");
        }
    }

    private class AgentA3NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.m_agent.m_a3_noise = (new Double(source.getValue()).doubleValue()) / 100.0;
            System.out.println("NoiseControlPanel: a3 noise = " + source.getValue() + "%");
        }
    }

    private class AgentA4NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.m_agent.m_a4_noise = (new Double(source.getValue()).doubleValue()) / 100.0;
            System.out.println("NoiseControlPanel: a4 noise = " + source.getValue() + "%");
        }
    }

    private class AgentA5NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.m_agent.m_a5_noise = (new Double(source.getValue()).doubleValue()) / 100.0;
            System.out.println("NoiseControlPanel: a5 noise = " + source.getValue() + "%");
        }
    }

    private class AgentA6NoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            AGVsim.m_agent.m_a6_noise = (new Double(source.getValue()).doubleValue()) / 100.0;
            System.out.println("NoiseControlPanel: a6 noise = " + source.getValue() + "%");
        }
    }

    private class SensorRangeNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = (new Double(source.getValue()).doubleValue());
            AGVsim.m_agent.Qt.set(0, 0, val * val);
            System.out.println("NoiseControlPanel: sensor range noise sigma^2 = " + val);
        }
    }

    private class SensorBearingNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = (new Double(source.getValue()).doubleValue());
            AGVsim.m_agent.Qt.set(1, 1, val * val);
            System.out.println("NoiseControlPanel: sensor bearing noise sigma^2 = " + val);
        }
    }

    private class SensorSignatureNoiseHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double val = (new Double(source.getValue()).doubleValue());
            AGVsim.m_agent.Qt.set(2, 2, val * val);
            System.out.println("NoiseControlPanel: sensor signature noise sigma^2 = " + val);
        }
    }

}
