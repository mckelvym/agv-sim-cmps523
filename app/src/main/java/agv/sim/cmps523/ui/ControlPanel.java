// Mark McKelvy
// CMPS 523
// Final Project
// File: ControlPanel.java
package agv.sim.cmps523.ui;

import static agv.sim.cmps523.ui.GuiUtils.addToPanel;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.Values;
import agv.sim.cmps523.event.JComboBoxSelectedIntegerListener;
import agv.sim.cmps523.event.JComboBoxSelectionIndexListener;
import agv.sim.cmps523.event.JSliderDoubleValueListener;
import agv.sim.cmps523.type.ClickMode;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.border.BevelBorder;

public class ControlPanel extends JPanel {
    private static final int SIZE_X = 300;
    private static final int SIZE_Y = 150;
    private final JComboBox<String> framerateCombo;
    private final JCheckBox enableCorrectionCheckbox;
    private final JButton buildModelButton;
    private final JSlider translationalVelocitySlider;
    private final JSlider rotationalVelocitySlider;
    private final JSlider timeDeltaSlider;
    private final JComboBox<String> mouseModeCombo;
    private final Values values;
    private final JButton runButton;
    private final JToolBar simulationToolbar;
    private final JButton resetButton;
    private final JButton stepButton;
    private final JButton pauseButton;

    public ControlPanel(Values values) {
        this.values = requireNonNull(values);

        enableCorrectionCheckbox = new JCheckBox("Enable Correction/Resampling");
        enableCorrectionCheckbox.setSelected(true);

        buildModelButton = new JButton("Build model");
        final String[] labels = IntStream.range(0, ClickMode.values().length)
                .mapToObj(ClickMode::at)
                .map(ClickMode::getDescription)
                .toArray(String[]::new);

        mouseModeCombo = new JComboBox<>(labels);
        mouseModeCombo.addActionListener(new JComboBoxSelectionIndexListener(index -> values.setClickMode(ClickMode.at(index))));
        final String[] framerates = {"1", "2", "5", "10", "15", "20", "30", "45", "60"};
        framerateCombo = new JComboBox<>(framerates);
        framerateCombo.setSelectedIndex(asList(framerates).indexOf("30"));                        // set default selection to 5 fps
        translationalVelocitySlider = newSlider(1, 15, (int) values.getAgentTranslationalVelocity(), 2);
        rotationalVelocitySlider = newSlider(-10, 10, (int) -toDegrees(values.getAgentRotationalVelocity()), 5);
        rotationalVelocitySlider.setLabelTable(getRotationalVelocitySliderLabels());
        timeDeltaSlider = newSlider(1, 100, 10, 10);
        timeDeltaSlider.setLabelTable(getTimeDeltaSliderLabels());

        simulationToolbar = new JToolBar();
        simulationToolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
        // add reset button
        resetButton = add(simulationToolbar, new ActionButton("Reset", this::handleReset));
        // add step button
        stepButton = add(simulationToolbar, new ActionButton("Step", this::handleStep));
        stepButton.setEnabled(true);    // disable step button
        // add play button
        runButton = add(simulationToolbar, new ActionButton("Run", this::handleRun));
        runButton.setEnabled(true);    // disable play button
        pauseButton = add(simulationToolbar, new ActionButton("Pause", this::handlePause));  // add pause button
        pauseButton.setEnabled(false); // turn pause button off to begin with

        enableCorrectionCheckbox.addItemListener(e -> values.setEnableCorrection(!values.isEnableCorrection()));
        buildModelButton.addActionListener(e -> {
            values.setEnginePaused(true);
            stepButton.setEnabled(true);
            runButton.setEnabled(true);
            values.notifyEngineBuildRequested();
        });
        framerateCombo.addActionListener(new JComboBoxSelectedIntegerListener(speed -> {
            // FrameRateHandler listens to frame rate combo box for user's selection.
            // This listener changes the integer frameRate which is used when playing the simulation
            System.out.println("ControlPanel: fps = " + speed);
            values.setFramesPerSecond(speed);
        }));    // listen for user framerate choices
        translationalVelocitySlider.addChangeListener(new JSliderDoubleValueListener(value -> {
            values.setAgentTranslationalVelocity(value);
        }));
        rotationalVelocitySlider.addChangeListener(new JSliderDoubleValueListener(value -> {
            double radians = toRadians(-value);
            if (radians == 0.0) {
                radians = toRadians(0.1);
            }
            values.setAgentRotationalVelocity(radians);
            System.out.println("ControlPanel: rotational velocity = " + toDegrees(radians));
        }));
        timeDeltaSlider.addChangeListener(new JSliderDoubleValueListener(value -> values.setTimestampDelta(1.0 / value)));

        int baseX = 0;
        int baseY = 0;
        setLayout(new GridBagLayout());
        addToPanel(this, simulationToolbar, baseX, baseY, 1, 1);
        addToPanel(this, buildModelButton, baseX + 1, baseY, 1, 1);
        addToPanel(this, new JLabel("Click Mode:"), baseX + 2, baseY, 1, 1);
        addToPanel(this, mouseModeCombo, baseX + 3, baseY, 1, 1);
        baseY++;
        addToPanel(this, new JLabel("Translational Velocity (cm/sec):"), baseX, baseY, 1, 1);
        addToPanel(this, translationalVelocitySlider, baseX + 1, baseY, 1, 1);
        addToPanel(this, new JLabel("Time Delta:"), baseX + 2, baseY, 1, 1);
        addToPanel(this, timeDeltaSlider, baseX + 3, baseY, 1, 1);
        baseY++;
        addToPanel(this, new JLabel("Rotational Velocity (deg/sec):"), baseX, baseY, 1, 1);
        addToPanel(this, rotationalVelocitySlider, baseX + 1, baseY, 1, 1);
        addToPanel(this, new JLabel("FPS:"), baseX + 2, baseY, 1, 1);
        addToPanel(this, framerateCombo, baseX + 3, baseY, 1, 1);
        baseY++;
        addToPanel(this, enableCorrectionCheckbox, baseX, baseY, 2, 1);
    }

    private static JButton add(JToolBar toolBar, Action action) {
        final JButton button = toolBar.add(action);
        button.setFont(new Font("Helvetica", Font.BOLD, 11));
        return button;
    }

    private static JSlider newSlider(int min, int max, int value, int majorTickSpacing) {
        final JSlider jSlider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        jSlider.setMajorTickSpacing(majorTickSpacing);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        return jSlider;
    }

    private static Hashtable<Integer, JLabel> getRotationalVelocitySliderLabels() {
        Hashtable<Integer, JLabel> table = new Hashtable<>();
        table.put(-10, new JLabel("10"));
        table.put(0, new JLabel("0"));
        table.put(10, new JLabel("-10"));
        return table;
    }

    private static Hashtable<Integer, JLabel> getTimeDeltaSliderLabels() {
        Hashtable<Integer, JLabel> table = new Hashtable<>();
        table.put(1, new JLabel("1.0"));
        table.put(100, new JLabel("0.01"));
        return table;
    }

    public static int getSizeX() {
        return SIZE_X;
    }

    public static int getSizeY() {
        return SIZE_Y;
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    private void handlePause() {
        values.setEnginePaused(true);            // pause the simulation
        runButton.setEnabled(true);        // enable the play button
        pauseButton.setEnabled(false);    // disable the pause button
        resetButton.setEnabled(true); //reenable reset button when program is repaused
        stepButton.setEnabled(true);    //reenable step button when program is repaused
    }

    private void handleStep() {
        resetButton.setEnabled(true);    //reactivate reset button
        values.notifyEngineRunStepRequested();
    }

    private void handleReset() {
        values.notifyEngineResetRequested();                // reset the system
        //reset_button.setEnabled(false);
        values.setEngineTimestep(0);
    }

    private void handleRun() {
        resetButton.setEnabled(false);   // disable reset button
        stepButton.setEnabled(false);    // disable step button
        runButton.setEnabled(false);        // disable play button
        pauseButton.setEnabled(true);    // only the pause button is left enabled
        values.setEnginePaused(false);            // unpause the program
    }
}
