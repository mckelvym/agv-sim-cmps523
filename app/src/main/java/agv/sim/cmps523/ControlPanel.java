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
    private static final int sizeX = 300;
    private static final int sizeY = 150;
    private static final JButton BUILD_BUTTON = new JButton("Build model");
    private static final JSlider TRANSLATIONAL_VELOCITY_SLIDER = new JSlider(JSlider.HORIZONTAL, 1, 15, 10);
    private static final JSlider ROTATIONAL_VELOCITY_SLIDER = new JSlider(JSlider.HORIZONTAL, -10, 10, 5);
    private static final JSlider TIME_DELTA_SLIDER = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
    private static double currTime = 0;
    private static String[] frameRates = {
            "1", "2", "5", "10", "15",
            "20", "30", "45", "60"
    };
    private static final JComboBox<String> FRAMERATE_COMBO = new JComboBox<>(getFrameRates());
    private static String[] mouseMode = {
            "Add Object",
            "Bot Actual",
            "Bot Belief"
    };
    private static JComboBox<String> mouseModeCombo = new JComboBox<>(getMouseMode());
    private static JCheckBox enableCorrectionCheckbox = new JCheckBox("Enable Correction/Resampling");
    private boolean isPaused = false; // Is simulation paused?
    private JButton resetButton;
    private JButton stepButton;
    private JButton runButton;
    private JButton pauseButton;
    private Font helvetica11Bold = new Font("Helvetica", Font.BOLD, 11);
    private JToolBar simulationToolbar;

    public ControlPanel() {
        super();    // Create the panel

        getEnableCorrectionCheckbox().setSelected(true);

        getBuildButton().addActionListener(new BuildButtonHandler());
        getFramerateCombo().setSelectedIndex(6);                        // set default selection to 5 fps
        getFramerateCombo().addActionListener(new FrameRateHandler());    // listen for user framerate choices
        getTranslationalVelocitySlider().addChangeListener(new TranslationalVelocityChoiceHandler());
        getRotationalVelocitySlider().addChangeListener(new RotationalVelocityChoiceHandler());
        getTimeDeltaSlider().addChangeListener(new TimeDeltaChoiceHandler());
        getEnableCorrectionCheckbox().addItemListener(new CorrectionCheckBoxHandler());

        getTranslationalVelocitySlider().setMajorTickSpacing(2);
        getTranslationalVelocitySlider().setPaintTicks(true);
        getTranslationalVelocitySlider().setPaintLabels(true);
        getRotationalVelocitySlider().setMajorTickSpacing(5);
        getRotationalVelocitySlider().setPaintTicks(true);
        getRotationalVelocitySlider().setPaintLabels(true);
        getTimeDeltaSlider().setMajorTickSpacing(10);
        getTimeDeltaSlider().setPaintTicks(false);
        getTimeDeltaSlider().setPaintLabels(true);
        Hashtable<Integer, JLabel> rotational_velocity_slider_label_table = new Hashtable<>();
        rotational_velocity_slider_label_table.put(-10, new JLabel("10"));
        rotational_velocity_slider_label_table.put(0, new JLabel("0"));
        rotational_velocity_slider_label_table.put(10, new JLabel("-10"));
        getRotationalVelocitySlider().setLabelTable((rotational_velocity_slider_label_table));
        Hashtable<Integer, JLabel> time_delta_slider_label_table = new Hashtable<>();
        time_delta_slider_label_table.put(1, new JLabel("1.0"));
        time_delta_slider_label_table.put(100, new JLabel("0.01"));
        getTimeDeltaSlider().setLabelTable((time_delta_slider_label_table));

        setSimulationToolbar(new JToolBar());
        getSimulationToolbar().setBorder(new BevelBorder(BevelBorder.RAISED));
        setResetButton(getSimulationToolbar().add(new ResetButtonAction("Reset")));  // add reset button
        getResetButton().setFont(getHelvetica11Bold());                                // Helvetica font
        setStepButton(getSimulationToolbar().add(new StepButtonAction("Step")));        // add step button
        getStepButton().setFont(getHelvetica11Bold());                                    // Helvetica font
        setRunButton(getSimulationToolbar().add(new RunButtonAction("Run")));        // add play button
        getRunButton().setFont(getHelvetica11Bold());                                    // Helvetica font
        setPauseButton(getSimulationToolbar().add(new PauseButtonAction("Pause")));  // add pause button
        getPauseButton().setFont(getHelvetica11Bold());                                // Helvetica font
        getPauseButton().setEnabled(false); // turn pause button off to begin with
        getStepButton().setEnabled(false);    // disable step button
        getRunButton().setEnabled(false);    // disable play button

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());
        GuiUtils.addToGridbag(this, getSimulationToolbar(), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getBuildButton(), base_x + 1, base_y, 1, 1);
        GuiUtils.addToGridbag(this, new JLabel("Click Mode:"), base_x + 2, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getMouseModeCombo(), base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Translational Velocity (cm/sec):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getTranslationalVelocitySlider(), base_x + 1, base_y, 1, 1);
        GuiUtils.addToGridbag(this, new JLabel("Time Delta:"), base_x + 2, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getTimeDeltaSlider(), base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Rotational Velocity (deg/sec):"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getRotationalVelocitySlider(), base_x + 1, base_y, 1, 1);
        GuiUtils.addToGridbag(this, new JLabel("FPS:"), base_x + 2, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getFramerateCombo(), base_x + 3, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, getEnableCorrectionCheckbox(), base_x, base_y, 2, 1);
    }

    public static int getSizeX() {
        return sizeX;
    }

    public static int getSizeY() {
        return sizeY;
    }

    public static JButton getBuildButton() {
        return BUILD_BUTTON;
    }

    public static JSlider getTranslationalVelocitySlider() {
        return TRANSLATIONAL_VELOCITY_SLIDER;
    }

    public static JSlider getRotationalVelocitySlider() {
        return ROTATIONAL_VELOCITY_SLIDER;
    }

    public static JSlider getTimeDeltaSlider() {
        return TIME_DELTA_SLIDER;
    }

    public static double getCurrTime() {
        return currTime;
    }

    public static void setCurrTime(double currTime) {
        ControlPanel.currTime = currTime;
    }

    public static String[] getFrameRates() {
        return frameRates;
    }

    public static void setFrameRates(String[] frameRates) {
        ControlPanel.frameRates = frameRates;
    }

    public static JComboBox<String> getFramerateCombo() {
        return FRAMERATE_COMBO;
    }

    public static String[] getMouseMode() {
        return mouseMode;
    }

    public static void setMouseMode(String[] mouseMode) {
        ControlPanel.mouseMode = mouseMode;
    }

    public static JComboBox<String> getMouseModeCombo() {
        return mouseModeCombo;
    }

    public static void setMouseModeCombo(JComboBox<String> mouseModeCombo) {
        ControlPanel.mouseModeCombo = mouseModeCombo;
    }

    public static JCheckBox getEnableCorrectionCheckbox() {
        return enableCorrectionCheckbox;
    }

    public static void setEnableCorrectionCheckbox(JCheckBox enableCorrectionCheckbox) {
        ControlPanel.enableCorrectionCheckbox = enableCorrectionCheckbox;
    }

    public Dimension getMinimumSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    public Dimension getPreferredSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    public double getCurrentTranslationalVelocity() {
        return Integer.valueOf(getTranslationalVelocitySlider().getValue()).doubleValue();
    }

    public double getCurrentRotationalVelocity() {
        return Math.toRadians(-(Integer.valueOf(getRotationalVelocitySlider().getValue()).doubleValue()));
    }

    // Getter and Setter functions for the paused property
    public boolean isNotPaused() {
        return !isPaused();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean val) {
        isPaused = val;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public void setResetButton(JButton resetButton) {
        this.resetButton = resetButton;
    }

    public JButton getStepButton() {
        return stepButton;
    }

    public void setStepButton(JButton stepButton) {
        this.stepButton = stepButton;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public void setRunButton(JButton runButton) {
        this.runButton = runButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public void setPauseButton(JButton pauseButton) {
        this.pauseButton = pauseButton;
    }

    public Font getHelvetica11Bold() {
        return helvetica11Bold;
    }

    public void setHelvetica11Bold(Font helvetica11Bold) {
        this.helvetica11Bold = helvetica11Bold;
    }

    public JToolBar getSimulationToolbar() {
        return simulationToolbar;
    }

    public void setSimulationToolbar(JToolBar simulationToolbar) {
        this.simulationToolbar = simulationToolbar;
    }

    // FrameRateHandler listens to frame rate combo box for user's selection.
    // This listener changes the integer frameRate which is used when playing the simulation
    private static class FrameRateHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String s = GuiUtils.getJComboBoxSelectedItem(e);
            int speed = Integer.parseInt(s);
            System.out.println("ControlPanel: fps = " + speed);
            Engine.setFps(speed);
        }
    }

    private static class BuildButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AGVsim.getEngine().buildArchitecture();
        }
    }

    private static class TranslationalVelocityChoiceHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            double vel = Integer.valueOf(source.getValue()).doubleValue();
            AGVsim.getAgent().setTranslationalVelocity(vel);
            System.out.println("ControlPanel: translational velocity = " + vel);
            AGVsim.getTestbedview().repaint();
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
            AGVsim.getAgent().setRotationalVelocity(vel);
            System.out.println("ControlPanel: rotational velocity = " + Math.toDegrees(vel));
            AGVsim.getTestbedview().repaint();
        }
    }

    private static class TimeDeltaChoiceHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            Engine.setDeltaT(1.0 / (Integer.valueOf(source.getValue()).doubleValue()));
            System.out.println("ControlPanel: time delta = " + Engine.getDeltaT());
        }
    }

    // ResetButtonAction contains the orders for when the reset button is pressed
    private static class ResetButtonAction extends AbstractAction {

        public ResetButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { // When reset is pressed
            AGVsim.getEngine().resetSystem();                // reset the system
            //reset_button.setEnabled(false);
            AGVsim.getLogger().init();
            setCurrTime(0);
        }
    }

    private static class CorrectionCheckBoxHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            AGVsim.getAgent().setEnableCorrection(e.getStateChange() != ItemEvent.DESELECTED);
        }
    }

    //class StepButtonAction contains the orders for when the step button is pressed
    private class StepButtonAction extends AbstractAction {

        public StepButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { //When step button is pressed
            AGVsim.getEngine().run1Frame();
            getResetButton().setEnabled(true);    //reactivate reset button
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
                getResetButton().setEnabled(false);   // disable reset button
                getStepButton().setEnabled(false);    // disable step button
                getRunButton().setEnabled(false);        // disable play button
                getPauseButton().setEnabled(true);    // only the pause button is left enabled
                setPaused(false);            // unpause the program
                long beforeTime = System.currentTimeMillis(); // from Davision, KGPJ, p. 23
                int nuruns = 0;
                while (isNotPaused() && nuruns < 9000) {        //while program is running
                    AGVsim.getEngine().run1Frame();    //run program 1 step and delay
                    AGVsim.getLogger().saveTime(getCurrTime());
                    setCurrTime(getCurrTime() + Engine.getDeltaT());
                    long delay = 1000 / Engine.getFps(); //according to  framerate
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
                getResetButton().setEnabled(true); //reenable reset button when program is repaused
                getStepButton().setEnabled(true);    //reenable step button when program is repaused
            }
        }
    }    // Start the thread to run the simulation

    //class PauseButtonAction contains the orders for when the pause button is pressed
    private class PauseButtonAction extends AbstractAction {

        public PauseButtonAction(String text) {
            super(text);
        }

        public void actionPerformed(ActionEvent e) { // If pause button is clicked
            setPaused(true);            // pause the simulation
            getRunButton().setEnabled(true);        // enable the play button
            getPauseButton().setEnabled(false);    // disable the pause button
        }
    }
}
