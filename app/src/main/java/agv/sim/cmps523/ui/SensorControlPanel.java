// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorControlPanel.java
package agv.sim.cmps523.ui;

import static agv.sim.cmps523.type.SensorNoiseProbabilityType.*;
import static agv.sim.cmps523.ui.GuiUtils.addToWindow;
import static agv.sim.cmps523.ui.GuiUtils.centerOnScreen;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.Agent;
import agv.sim.cmps523.Values;
import agv.sim.cmps523.event.JComboBoxSelectedDoubleListener;
import agv.sim.cmps523.event.JComboBoxSelectedStringListener;
import agv.sim.cmps523.event.JSliderDoubleValueListener;
import agv.sim.cmps523.type.SensorNoiseProbabilityType;
import com.google.common.collect.Maps;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.swing.*;

public class SensorControlPanel extends JDialog {
    private final Map<SensorNoiseProbabilityType, JSlider> sensorNoiseProbabilitySlider;
    private final JSlider sigmaHitSlider;
    private final JSlider lambdaShortSlider;
    private final JComboBox<String> sensorMaxRangeCombo;
    private final JComboBox<String> sensorAngResCombo;
    private final JComboBox<String> sensorDisplayCombo;
    private final JComboBox<String> sensorReturnDisplayCombo;
    private final JButton closeButton;
    private final Values values;

    public SensorControlPanel(Values values) {
        this.values = requireNonNull(values);
        sensorMaxRangeCombo = new JComboBox<>(new String[]{"200", "300", "500", "1000"});
        sensorAngResCombo = new JComboBox<>(new String[]{"5", "2", "1"});
        sensorDisplayCombo = new JComboBox<>(new String[]{"Yes", "No"});
        sensorReturnDisplayCombo = new JComboBox<>(new String[]{"Yes", "No"});
        sensorNoiseProbabilitySlider = Maps.newLinkedHashMap();
        forEachSensorNoiseProbabilityType(type ->
                sensorNoiseProbabilitySlider.put(
                        type, newSlider(100, 0, 5)));
        sigmaHitSlider = newSlider(30, 1, 5);
        lambdaShortSlider = newSlider(30, 1, 10);
        closeButton = new JButton("Close");
    }

    private static JSlider newSlider(int max, int initialValue, int majorTickSpacing) {
        final JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, max, initialValue);
        jSlider.setMajorTickSpacing(majorTickSpacing);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        return jSlider;
    }

    public void display() {
        sensorMaxRangeCombo.setSelectedIndex(2);
        sensorMaxRangeCombo.addActionListener(new JComboBoxSelectedDoubleListener(Agent.getCurrent().getSensor()::setMaxRange));
        sensorAngResCombo.setSelectedIndex(0);
        sensorAngResCombo.addActionListener(new JComboBoxSelectedDoubleListener(Agent.getCurrent().getSensor()::setAngularResolution));
        sensorDisplayCombo.addActionListener(new JComboBoxSelectedStringListener(text -> {
            final boolean drawSensorBeams = "Yes".equals(text);
            values.setSensorBeamsDraw(drawSensorBeams);
        }));
        sensorReturnDisplayCombo.addActionListener(new JComboBoxSelectedStringListener(text -> {
            final boolean drawSensorReturnBeams = "Yes".equals(text);
            values.setSensorReturnBeamsDraw(drawSensorReturnBeams);
        }));

        forEachSensorNoiseProbabilityTypeSlider((type, slider) ->
                slider.addChangeListener(new JSliderDoubleValueListener(value -> {
                    values.setSensorNoiseProbability(type, value);
                })));
        sigmaHitSlider.addChangeListener(new JSliderDoubleValueListener(
                values::setSigmaHit));
        lambdaShortSlider.addChangeListener(new JSliderDoubleValueListener(
                values::setLambdaShort));

        Hashtable<Integer, JLabel> noneLessMore = new Hashtable<>();
        noneLessMore.put((0), new JLabel("None"));
        noneLessMore.put((50), new JLabel("Less"));
        noneLessMore.put((100), new JLabel("More"));

        forEachSensorNoiseProbabilityTypeSlider((type, slider) -> slider.setLabelTable(noneLessMore));

        closeButton.addActionListener(e -> dispose());

        int baseX = 0;
        AtomicInteger baseY = new AtomicInteger(0);
        setLayout(new GridBagLayout());

        addToWindow(this, new JLabel("Sensor Configuration"), baseX, baseY.get(), 2, 1);
        baseY.addAndGet(2);
        addToWindow(this, new JLabel("Angular Resolution:"), baseX, baseY.get(), 1, 1);
        addToWindow(this, sensorAngResCombo, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        addToWindow(this, new JLabel("Max Range:"), baseX, baseY.get(), 1, 1);
        addToWindow(this, sensorMaxRangeCombo, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        addToWindow(this, new JLabel("Display Beams:"), baseX, baseY.get(), 1, 1);
        addToWindow(this, sensorDisplayCombo, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        addToWindow(this, new JLabel("Display Returned Beams:"), baseX, baseY.get(), 1, 1);
        addToWindow(this, sensorReturnDisplayCombo, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        forEachSensorNoiseProbabilityTypeSlider((type, slider) -> {
            addToWindow(this, new JLabel(type.name() + ":"), baseX, baseY.get(), 1, 1);
            addToWindow(this, slider, baseX + 1, baseY.get(), 1, 1);
            baseY.incrementAndGet();
        });
        addToWindow(this, new JLabel("Sigma_hit:"), baseX, baseY.get(), 1, 1);
        addToWindow(this, sigmaHitSlider, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        addToWindow(this, new JLabel("Lambda_short:"), baseX, baseY.get(), 1, 1);
        addToWindow(this, lambdaShortSlider, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        addToWindow(this, closeButton, baseX, baseY.get(), 2, 1);

        pack();
        centerOnScreen(this);
        setVisible(true);
    }

    private void forEachSensorNoiseProbabilityType(Consumer<SensorNoiseProbabilityType> sensorNoiseProbabilityTypeConsumer) {
        Stream.of(Z_HIT, Z_SHORT, Z_MAX, Z_RAND).forEach(sensorNoiseProbabilityTypeConsumer::accept);
    }

    private void forEachSensorNoiseProbabilityTypeSlider(BiConsumer<SensorNoiseProbabilityType, JSlider> sensorNoiseProbabilityTypeSliderConsumer) {
        forEachSensorNoiseProbabilityType(type -> sensorNoiseProbabilityTypeSliderConsumer.accept(type, sensorNoiseProbabilitySlider.get(type)));
    }
}
