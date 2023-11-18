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
    static final int sizeX = 300, sizeY = 150;
    static final JButton BUILD_BUTTON = new JButton("Build model");
    static final JSlider TRANSLATIONAL_VELOCITY_SLIDER = new JSlider(JSlider.HORIZONTAL, 1, 15, 10);
    static final JSlider ROTATIONAL_VELOCITY_SLIDER = new JSlider(JSlider.HORIZONTAL, -10, 10, 5);
    static final JSlider TIME_DELTA_SLIDER = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
    static double currTime = 0;
    static String[] frameRates = {
            "1", "2", "5", "10", "15",
            "20", "30", "45", "60"
    };
    static final JComboBox<String> FRAMERATE_COMBO = new JComboBox<>(frameRates);
    static String[] mouseMode = {
            "Add Object",
            "Bot Actual",
            "Bot Belief"
    };
    static JComboBox<String> mouseModeCombo = new JComboBox<>(mouseMode);
    static JCheckBox enableCorrectionCheckbox = new JCheckBox("Enable Correction/Resampling");
    boolean isPaused = false; // Is simulation paused?
    JButton resetButton;
    JButton stepButton;
    JButton runButton;
    JButton pauseButton;
    Font helvetica11Bold = new Font("Helvetica", Font.BOLD, 11);
    JToolBar simulationToolbar;

    public ControlPanel() {
        super();    // Create the panel

        enableCorrectionCheckbox.setSelected(true);

        BUILD_BUTTON.addActionListener(new BuildButtonHandler());
        FRAMERATE_COMBO.setSelectedIndex(6);                        // set default selection to 5 fps
        FRAMERATE_COMBO.addActionListener(new FrameRateHandler());    // listen for user framerate choices
        TRANSLATIONAL_VELOCITY_SLIDER.addChangeListener(new TranslationalVelocityChoiceHandler());
        ROTATIONAL_VELOCITY_SLIDER.addChangeListener(new RotationalVelocityChoiceHandler());
        TIME_DELTA_SLIDER.addChangeListener(new TimeDeltaChoiceHandler());
        enableCorrectionCheckbox.addItemListener(new CorrectionCheckBoxHandler());

        TRANSLATIONAL_VELOCITY_SLIDER.setMajorTickSpacing(2);
        TRANSLATIONAL_VELOCITY_SLIDER.setPaintTicks(true);
        TRANSLATIONAL_VELOCITY_SLIDER.setPaintLabels(true);
        ROTATIONAL_VELOCITY_SLIDER.setMajorTickSpacing(5);
        ROTATIONAL_VELOCITY_SLIDER.setPaintTicks(true);
        ROTATIONAL_VELOCITY_SLIDER.setPaintLabels(true);
        TIME_DELTA_SLIDER.setMajorTickSpacing(10);
        TIME_DELTA_SLIDER.setPaintTicks(false);
        TIME_DELTA_SLIDER.setPaintLabels(true);
        Hashtable<Integer, JLabel> rotational_velocity_slider_label_table = new Hashtable<>();
        rotational_velocity_slider_label_table.put(-10, new JLabel("10"));
        rotational_velocity_slider_label_table.put(0, new JLabel("0"));
        rotational_velocity_slider_label_table.put(10, new JLabel("-10"));
        ROTATIONAL_VELOCITY_SLIDER.setLabelTable((rotational_velocity_slider_label_table));
        Hashtable<Integer, JLabel> time_delta_slider_label_table = new Hashtable<>();
        time_delta_slider_label_table.put(1, new JLabel("1.0"));
        time_delta_slider_label_table.put(100, new JLabel("0.01"));
        TIME_DELTA_SLIDER.setLabelTable((time_delta_slider_label_table));

        simulationToolbar = new JToolBar();
        simulationToolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
        resetButton = simulationToolbar.add(new ResetButtonAction("Reset"));  // add reset button
        resetButton.setFont(helvetica11Bold);                                // Helvetica font
        stepButton = simulationToolbar.add(new StepButtonAction("Step"));        // add step button
        stepButton.setFont(helvetica11Bold);                                    // Helvetica font
        runButton = simulationToolbar.add(new RunButtonAction("Run"));        // add play button
        runButton.setFont(helvetica11Bold);                                    // Helvetica font
        pauseButton = simulationToolbar.add(new PauseButtonAction("Pause"));  // add pause button
        pauseButton.setFont(helvetica11Bold);                                // Helvetica font
        pauseButton.setEnabled(false); // turn pause button off to begin with
        stepButton.setEnabled(false);    // disable step button
        runButton.setEnabled(false);    // disable play button

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());
        GuiUtils.add_to_gridbag(this, simulationToolbar, base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, BUILD_BUTTON, base_x + 1, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, new JLabel("Click Mode:"), base_x + 2, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, mouseModeCombo, base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Translational Velocity (cm/sec):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, TRANSLATIONAL_VELOCITY_SLIDER, base_x + 1, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, new JLabel("Time Delta:"), base_x + 2, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, TIME_DELTA_SLIDER, base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Rotational Velocity (deg/sec):"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, ROTATIONAL_VELOCITY_SLIDER, base_x + 1, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, new JLabel("FPS:"), base_x + 2, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, FRAMERATE_COMBO, base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, enableCorrectionCheckbox, base_x, base_y, 2, 1);
    }

    public Dimension getMinimumSize() {
        return new Dimension(sizeX, sizeY);
    }

    public Dimension getPreferredSize() {
        return new Dimension(sizeX, sizeY);
    }

    public double get_current_translational_velocity() {
        return Integer.valueOf(TRANSLATIONAL_VELOCITY_SLIDER.getValue()).doubleValue();
    }

    public double get_current_rotational_velocity() {
        return Math.toRadians(-(Integer.valueOf(ROTATIONAL_VELOCITY_SLIDER.getValue()).doubleValue()));
    }

    // Getter and Setter functions for the paused property
    public boolean isNotPaused() {
        return !isPaused;
    }

    public void set_paused(boolean val) {
        isPaused = val;
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
            Engine.deltaT = 1.0 / (Integer.valueOf(source.getValue()).doubleValue());
            System.out.println("ControlPanel: time delta = " + Engine.deltaT);
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
            currTime = 0;
        }
    }

    private static class CorrectionCheckBoxHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            AGVsim.agent.enableCorrection = e.getStateChange() != ItemEvent.DESELECTED;
        }
    }

    //class StepButtonAction contains the orders for when the step button is pressed
    private class StepButtonAction extends AbstractAction {

        public StepButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { //When step button is pressed
            AGVsim.engine.run_1_frame();
            resetButton.setEnabled(true);    //reactivate reset button
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
                resetButton.setEnabled(false);   // disable reset button
                stepButton.setEnabled(false);    // disable step button
                runButton.setEnabled(false);        // disable play button
                pauseButton.setEnabled(true);    // only the pause button is left enabled
                set_paused(false);            // unpause the program
                long beforeTime = System.currentTimeMillis(); // from Davision, KGPJ, p. 23
                int nuruns = 0;
                while (isNotPaused() && nuruns < 9000) {        //while program is running
                    AGVsim.engine.run_1_frame();    //run program 1 step and delay
                    AGVsim.logger.save_time(currTime);
                    currTime += Engine.deltaT;
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
                resetButton.setEnabled(true); //reenable reset button when program is repaused
                stepButton.setEnabled(true);    //reenable step button when program is repaused
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
            runButton.setEnabled(true);        // enable the play button
            pauseButton.setEnabled(false);    // disable the pause button
        }
    }
}
