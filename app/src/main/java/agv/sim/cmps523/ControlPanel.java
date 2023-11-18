// Mark McKelvy
// CMPS 523
// Final Project
// File: ControlPanel.java
package agv.sim.cmps523;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel {
    static final int size_x = 300, size_y = 150;
    static final JButton build_button = new JButton("Build model");
    static final JSlider translational_velocity_slider = new JSlider(JSlider.HORIZONTAL, 1, 15, 10);
    static final JSlider rotational_velocity_slider = new JSlider(JSlider.HORIZONTAL, -10, 10, 5);
    static final JSlider time_delta_slider = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
    static double curr_time = 0;
    static String[] frame_rates = {
            "1", "2", "5", "10", "15",
            "20", "30", "45", "60"
    };
    static final JComboBox<String> framerate_combo = new JComboBox<>(frame_rates);
    static String[] mouse_mode = {
            "Add Object",
            "Bot Actual",
            "Bot Belief"
    };
    static JComboBox<String> mouse_mode_combo = new JComboBox<>(mouse_mode);
    static JCheckBox enable_correction_checkbox = new JCheckBox("Enable Correction/Resampling");
    boolean is_paused = false; // Is simulation paused?
    JButton reset_button;
    JButton step_button;
    JButton run_button;
    JButton pause_button;
    Font helvetica_11_bold = new Font("Helvetica", Font.BOLD, 11);
    JToolBar simulation_toolbar;

    public ControlPanel() {
        super();    // Create the panel

        enable_correction_checkbox.setSelected(true);

        build_button.addActionListener(new BuildButtonHandler());
        framerate_combo.setSelectedIndex(6);                        // set default selection to 5 fps
        framerate_combo.addActionListener(new FrameRateHandler());    // listen for user framerate choices
        translational_velocity_slider.addChangeListener(new TranslationalVelocityChoiceHandler());
        rotational_velocity_slider.addChangeListener(new RotationalVelocityChoiceHandler());
        time_delta_slider.addChangeListener(new TimeDeltaChoiceHandler());
        enable_correction_checkbox.addItemListener(new CorrectionCheckBoxHandler());

        translational_velocity_slider.setMajorTickSpacing(2);
        translational_velocity_slider.setPaintTicks(true);
        translational_velocity_slider.setPaintLabels(true);
        rotational_velocity_slider.setMajorTickSpacing(5);
        rotational_velocity_slider.setPaintTicks(true);
        rotational_velocity_slider.setPaintLabels(true);
        time_delta_slider.setMajorTickSpacing(10);
        time_delta_slider.setPaintTicks(false);
        time_delta_slider.setPaintLabels(true);
        Hashtable<Integer, JLabel> rotational_velocity_slider_label_table = new Hashtable<>();
        rotational_velocity_slider_label_table.put(-10, new JLabel("10"));
        rotational_velocity_slider_label_table.put(0, new JLabel("0"));
        rotational_velocity_slider_label_table.put(10, new JLabel("-10"));
        rotational_velocity_slider.setLabelTable((rotational_velocity_slider_label_table));
        Hashtable<Integer, JLabel> time_delta_slider_label_table = new Hashtable<>();
        time_delta_slider_label_table.put(1, new JLabel("1.0"));
        time_delta_slider_label_table.put(100, new JLabel("0.01"));
        time_delta_slider.setLabelTable((time_delta_slider_label_table));

        simulation_toolbar = new JToolBar();
        simulation_toolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
        reset_button = simulation_toolbar.add(new ResetButtonAction("Reset"));  // add reset button
        reset_button.setFont(helvetica_11_bold);                                // Helvetica font
        step_button = simulation_toolbar.add(new StepButtonAction("Step"));        // add step button
        step_button.setFont(helvetica_11_bold);                                    // Helvetica font
        run_button = simulation_toolbar.add(new RunButtonAction("Run"));        // add play button
        run_button.setFont(helvetica_11_bold);                                    // Helvetica font
        pause_button = simulation_toolbar.add(new PauseButtonAction("Pause"));  // add pause button
        pause_button.setFont(helvetica_11_bold);                                // Helvetica font
        pause_button.setEnabled(false); // turn pause button off to begin with
        step_button.setEnabled(false);    // disable step button
        run_button.setEnabled(false);    // disable play button

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());
        GuiUtils.add_to_gridbag(this, simulation_toolbar, base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, build_button, base_x + 1, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, new JLabel("Click Mode:"), base_x + 2, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, mouse_mode_combo, base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Translational Velocity (cm/sec):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, translational_velocity_slider, base_x + 1, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, new JLabel("Time Delta:"), base_x + 2, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, time_delta_slider, base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Rotational Velocity (deg/sec):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, rotational_velocity_slider, base_x + 1, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, new JLabel("FPS:"), base_x + 2, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, framerate_combo, base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, enable_correction_checkbox, base_x, base_y, 2, 1);
    }

    public Dimension getMinimumSize() {
        return new Dimension(size_x, size_y);
    }

    public Dimension getPreferredSize() {
        return new Dimension(size_x, size_y);
    }

    public double get_current_translational_velocity() {
        return Integer.valueOf(translational_velocity_slider.getValue()).doubleValue();
    }

    public double get_current_rotational_velocity() {
        return Math.toRadians(-(Integer.valueOf(rotational_velocity_slider.getValue()).doubleValue()));
    }

    // Getter and Setter functions for the paused property
    public boolean isNotPaused() {
        return !is_paused;
    }

    public void set_paused(boolean val) {
        is_paused = val;
    }

    // FrameRateHandler listens to frame rate combo box for user's selection.
    // This listener changes the integer frameRate which is used when playing the simulation
    private static class FrameRateHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String s = GuiUtils.getJComboBoxSelectedItem(e);
            int speed = Integer.parseInt(s);
            System.out.println("ControlPanel: fps = " + speed);
            Engine.fps = speed;
        }
    }

    private static class BuildButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AGVsim.engine.build_architecture();
        }
    }

    private static class TranslationalVelocityChoiceHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double vel = Integer.valueOf(source.getValue()).doubleValue();
            AGVsim.agent.set_translational_velocity(vel);
            System.out.println("ControlPanel: translational velocity = " + vel);
            AGVsim.testbedview.repaint();
        }
    }

    private static class RotationalVelocityChoiceHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double vel = Math.toRadians(-(Integer.valueOf(source.getValue()).doubleValue()));
            if (vel == 0.0) {
                source.setValue(1);
                vel = Math.toRadians(1.0);
            }
            AGVsim.agent.set_rotational_velocity(vel);
            System.out.println("ControlPanel: rotational velocity = " + Math.toDegrees(vel));
            AGVsim.testbedview.repaint();
        }
    }

    private static class TimeDeltaChoiceHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Engine.delta_t = 1.0 / (Integer.valueOf(source.getValue()).doubleValue());
            System.out.println("ControlPanel: time delta = " + Engine.delta_t);
        }
    }

    // ResetButtonAction contains the orders for when the reset button is pressed
    private static class ResetButtonAction extends AbstractAction {

        public ResetButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { // When reset is pressed
            AGVsim.engine.reset_system();                // reset the system
            //reset_button.setEnabled(false);
            AGVsim.logger.init();
            curr_time = 0;
        }
    }

    private static class CorrectionCheckBoxHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            AGVsim.agent.enable_correction = e.getStateChange() != ItemEvent.DESELECTED;
        }
    }

    //class StepButtonAction contains the orders for when the step button is pressed
    private class StepButtonAction extends AbstractAction {

        public StepButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { //When step button is pressed
            AGVsim.engine.run_1_frame();
            reset_button.setEnabled(true);    //reactivate reset button
        }
    }                        //run simulation one step

    //class RunButtonAction contains the orders for when the play button is pressed
    private class RunButtonAction extends AbstractAction {

        public RunButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) {    // If play button is clicked
            RunButtonThread thread = new RunButtonThread();    // Create a new thread
            thread.setPriority(Thread.MAX_PRIORITY);    // with max priority
            thread.start();
        }

        private class RunButtonThread extends Thread { //Thread to run simulation
            public void run() {
                reset_button.setEnabled(false);   // disable reset button
                step_button.setEnabled(false);    // disable step button
                run_button.setEnabled(false);        // disable play button
                pause_button.setEnabled(true);    // only the pause button is left enabled
                set_paused(false);            // unpause the program
                long beforeTime = System.currentTimeMillis(); // from Davision, KGPJ, p. 23
                int nuruns = 0;
                while (isNotPaused() && nuruns < 9000) {        //while program is running
                    AGVsim.engine.run_1_frame();    //run program 1 step and delay
                    AGVsim.logger.save_time(curr_time);
                    curr_time += Engine.delta_t;
                    long delay = 1000 / Engine.fps; //according to  framerate
                    long timeDiff = System.currentTimeMillis() - beforeTime;
                    long sleepTime = delay - timeDiff;
                    if (sleepTime < 0) sleepTime = 5; // be nice
                    try {
                        Thread.sleep(sleepTime);
                    } //sleep until delay is ready for next cycle
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    beforeTime = System.currentTimeMillis();
                    nuruns++;
                }
                reset_button.setEnabled(true); //reenable reset button when program is repaused
                step_button.setEnabled(true);    //reenable step button when program is repaused
            }
        }
    }    // Start the thread to run the simulation

    //class PauseButtonAction contains the orders for when the pause button is pressed
    private class PauseButtonAction extends AbstractAction {

        public PauseButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { // If pause button is clicked
            set_paused(true);            // pause the simulation
            run_button.setEnabled(true);        // enable the play button
            pause_button.setEnabled(false);    // disable the pause button
        }
    }
}
