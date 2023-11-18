// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorControlPanel.java
package agv.sim.cmps523;

import static agv.sim.cmps523.GuiUtils.getValueDouble;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SensorControlPanel extends JDialog {
    static final PrintStream cout = System.out;
    static final JSlider z_hit_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider z_short_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider z_max_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider z_rand_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    static final JSlider sigma_hit_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    static final JSlider lambda_short_slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private static final long serialVersionUID = 1L;
    static String[] sensor_max_range = {
            "200", "300", "500", "1000"
    };
    static JComboBox sensor_max_range_combo = new JComboBox(sensor_max_range);
    static String[] sensor_ang_res = {
            "5", "2", "1"
    };
    static JComboBox sensor_ang_res_combo = new JComboBox(sensor_ang_res);
    static String[] sensor_display = {
            "Yes", "No"
    };
    static JComboBox sensor_display_combo = new JComboBox(sensor_display);
    static String[] sensor_return_display = {
            "Yes", "No"
    };
    static JComboBox sensor_return_display_combo = new JComboBox(sensor_return_display);
    JButton close = new JButton("Close");

    SensorControlPanel() {
        sensor_max_range_combo.setSelectedIndex(2);
        sensor_max_range_combo.addActionListener(new SensorMaxRangeHandler());
        sensor_ang_res_combo.setSelectedIndex(0);
        sensor_ang_res_combo.addActionListener(new SensorAngResHandler());
        sensor_display_combo.addActionListener(new SensorDisplayHandler());
        sensor_return_display_combo.addActionListener(new SensorReturnDisplayHandler());

        z_hit_slider.setMajorTickSpacing(5);
        z_hit_slider.setPaintTicks(true);
        z_hit_slider.setPaintLabels(true);

        z_short_slider.setMajorTickSpacing(5);
        z_short_slider.setPaintTicks(true);
        z_short_slider.setPaintLabels(true);

        z_max_slider.setMajorTickSpacing(5);
        z_max_slider.setPaintTicks(true);
        z_max_slider.setPaintLabels(true);

        z_rand_slider.setMajorTickSpacing(5);
        z_rand_slider.setPaintTicks(true);
        z_rand_slider.setPaintLabels(true);

        sigma_hit_slider.setMajorTickSpacing(5);
        sigma_hit_slider.setPaintTicks(true);
        sigma_hit_slider.setPaintLabels(true);

        lambda_short_slider.setMajorTickSpacing(10);
        lambda_short_slider.setPaintTicks(true);
        lambda_short_slider.setPaintLabels(true);

        z_hit_slider.addChangeListener(new ZHitHandler());
        z_short_slider.addChangeListener(new ZShortHandler());
        z_max_slider.addChangeListener(new ZMaxHandler());
        z_rand_slider.addChangeListener(new ZRandHandler());
        sigma_hit_slider.addChangeListener(new SigmaHitHandler());
        lambda_short_slider.addChangeListener(new LambdaShortHandler());

        Hashtable z_hit_labels = new Hashtable();
        z_hit_labels.put(Integer.valueOf(0), new JLabel("None"));
        z_hit_labels.put(Integer.valueOf(50), new JLabel("Less"));
        z_hit_labels.put(Integer.valueOf(100), new JLabel("More"));
        z_hit_slider.setLabelTable(z_hit_labels);

        Hashtable z_short_labels = new Hashtable();
        z_short_labels.put(Integer.valueOf(0), new JLabel("None"));
        z_short_labels.put(Integer.valueOf(50), new JLabel("Less"));
        z_short_labels.put(Integer.valueOf(100), new JLabel("More"));
        z_short_slider.setLabelTable(z_short_labels);

        Hashtable z_max_labels = new Hashtable();
        z_max_labels.put(Integer.valueOf(0), new JLabel("None"));
        z_max_labels.put(Integer.valueOf(50), new JLabel("Less"));
        z_max_labels.put(Integer.valueOf(100), new JLabel("More"));
        z_max_slider.setLabelTable(z_max_labels);

        Hashtable z_rand_labels = new Hashtable();
        z_rand_labels.put(Integer.valueOf(0), new JLabel("None"));
        z_rand_labels.put(Integer.valueOf(50), new JLabel("Less"));
        z_rand_labels.put(Integer.valueOf(100), new JLabel("More"));
        z_rand_slider.setLabelTable(z_rand_labels);


        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());

        GuiUtils.add_to_gridbag(this, new JLabel("Sensor Configuration"), base_x, base_y, 2, 1);
        base_y += 2;
        GuiUtils.add_to_gridbag(this, new JLabel("Angular Resolution:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_ang_res_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Max Range:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_max_range_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Display Beams:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_display_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Display Returned Beams:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sensor_return_display_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_hit:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, z_hit_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_short:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, z_short_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_max:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, z_max_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Z_rand:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, z_rand_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Sigma_hit:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, sigma_hit_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Lambda_short:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, lambda_short_slider, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, close, base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.center_on_screen(this);
        this.setVisible(true);
    }

    // Algorithm beam_range_finder_model page 158
    public static double get_sensor_noise_probability(int i) {
        switch (i) {
            case 0:
                return getValueDouble(z_hit_slider);
            case 1:
                return getValueDouble(z_short_slider);
            case 2:
                return getValueDouble(z_max_slider);
            case 3:
                return getValueDouble(z_rand_slider);
            default:
                return 0.0;
        }
    }

    public static double get_sigma_hit() {
        return getValueDouble(sigma_hit_slider);
    }

    public static double get_lambda_short() {
        return getValueDouble(lambda_short_slider);
    }

    private class SensorMaxRangeHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String range = (String) ((JComboBox) e.getSource()).getSelectedItem();
            AGVsim.m_agent.m_sensor.set_max_range(Double.valueOf(range));
            System.out.println("sensor range=" + range);
        }
    }

    private class SensorAngResHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String res = (String) ((JComboBox) e.getSource()).getSelectedItem();
            AGVsim.m_agent.m_sensor.set_angular_resolution(Double.valueOf(res));
            System.out.println("sensor angular resolution=" + res);
        }
    }

    private class SensorDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = (String) ((JComboBox) e.getSource()).getSelectedItem();
            AGVsim.m_testbedview.m_draw_sensor_beams = disp.equals("Yes");
            AGVsim.m_testbedview.repaint();
        }
    }

    private class SensorReturnDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = (String) ((JComboBox) e.getSource()).getSelectedItem();
            AGVsim.m_testbedview.m_draw_sensor_return_beams = disp.equals("Yes");
            AGVsim.m_testbedview.repaint();
        }
    }

    private class ZHitHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.m_agent.m_sensor.normalize_noise();
        }
    }

    private class ZShortHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.m_agent.m_sensor.normalize_noise();
        }
    }

    private class ZMaxHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.m_agent.m_sensor.normalize_noise();
        }
    }

    private class ZRandHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            AGVsim.m_agent.m_sensor.normalize_noise();
        }
    }

    private class SigmaHitHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            final double valueDouble = getValueDouble(source);
            AGVsim.m_agent.m_sensor.sigma_hit = valueDouble;
            cout.println("SIGMA_hit = " + valueDouble);
        }
    }

    private class LambdaShortHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            final double valueDouble = getValueDouble(source);
            AGVsim.m_agent.m_sensor.lambda_short = valueDouble;
            cout.println("LAMBDA_short = " + valueDouble);
        }
    }
}
