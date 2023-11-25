// Mark McKelvy
// CMPS 523
// Final Project
// File: NoiseControlPanel.java
package agv.sim.cmps523.ui;

import static agv.sim.cmps523.math.MathUtil.square;
import static agv.sim.cmps523.type.SensorNoiseType.*;
import static agv.sim.cmps523.ui.GuiUtils.addToWindow;
import static agv.sim.cmps523.ui.GuiUtils.centerOnScreen;
import static java.lang.Math.toRadians;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.Values;
import agv.sim.cmps523.event.JSliderDoubleValueListener;
import agv.sim.cmps523.event.JSliderPercentValueListener;
import agv.sim.cmps523.type.SensorNoiseType;
import com.google.common.collect.Maps;
import java.awt.GridBagLayout;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class NoiseControlPanel extends JDialog {
    private final int agentNoiseCount;
    private final JSlider testbedVNoiseSlider;
    private final JSlider testbedWNoiseSlider;
    private final Map<Integer, JSlider> agentNoiseSlider;
    private final Map<SensorNoiseType, JSlider> sensorNoiseSlider;
    private final JButton closeButton;
    private final Values values;

    public NoiseControlPanel(Values values) {
        this.values = requireNonNull(values);
        agentNoiseCount = values.getAlgorithmType().getAgentNoiseCount();
        testbedVNoiseSlider = newSlider(10, 2, 2);
        testbedWNoiseSlider = newSlider(5, 2, 2);
        agentNoiseSlider = Maps.newLinkedHashMap();
        forEachAgentNoise(index ->
                agentNoiseSlider.put(
                        index, newSlider(30, 1, 5)));
        sensorNoiseSlider = Maps.newLinkedHashMap();
        forEachSensorNoiseType(type ->
                sensorNoiseSlider.put(
                        type, newSlider(30, 1, 5)));
        closeButton = new JButton("Close");
    }

    private static JSlider newSlider(int max, int value, int majorTickSpacing) {
        final JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, max, value);
        jSlider.setMajorTickSpacing(majorTickSpacing);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        return jSlider;
    }

    public void display() {
        testbedVNoiseSlider.addChangeListener(new JSliderDoubleValueListener(
                values::setTranslationalNoise));
        testbedWNoiseSlider.addChangeListener(new JSliderDoubleValueListener(
                value -> values.setRotationalNoise(toRadians(value))));
        forEachAgentNoiseSlider((index, slider) -> {
            slider.addChangeListener(new JSliderPercentValueListener(
                    value -> values.setAlphaNoise(index, value / 100.0)));
        });
        forEachSensorNoiseTypeSlider((type, slider) -> {
            slider.addChangeListener(new JSliderDoubleValueListener(
                    value -> values.setSensorNoise(type, square(value))));
        });
        closeButton.addActionListener(e -> dispose());

        int baseX = 0;
        AtomicInteger baseY = new AtomicInteger(0);
        setLayout(new GridBagLayout());

        addToWindow(this, new JLabel("Noise Configuration"), baseX, baseY.get(), 2, 1);
        baseY.addAndGet(2);
        addToWindow(this, new JLabel("Testbed Translational Noise (cm/sec):"), baseX, baseY.get(), 1, 1);
        addToWindow(this, testbedVNoiseSlider, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        addToWindow(this, new JLabel("Testbed Rotational Noise (deg/sec):"), baseX, baseY.get(), 1, 1);
        addToWindow(this, testbedWNoiseSlider, baseX + 1, baseY.get(), 1, 1);
        baseY.incrementAndGet();
        forEachAgentNoiseSlider((index, slider) -> {
            addToWindow(this, new JLabel("Agent alpha " + index + " noise (%):"), baseX, baseY.get(), 1, 1);
            addToWindow(this, slider, baseX + 1, baseY.get(), 1, 1);
            baseY.incrementAndGet();
        });
        forEachSensorNoiseTypeSlider((type, slider) -> {
            final String name = type.name().toLowerCase();
            addToWindow(this, new JLabel("Sensor " + name + " noise sigma:"), baseX, baseY.get(), 1, 1);
            addToWindow(this, slider, baseX + 1, baseY.get(), 1, 1);
            baseY.incrementAndGet();
        });
        addToWindow(this, closeButton, baseX, baseY.get(), 2, 1);

        pack();
        centerOnScreen(this);
        setVisible(true);
    }

    private void forEachAgentNoise(Consumer<Integer> indexConsumer) {
        IntStream.rangeClosed(1, agentNoiseCount).forEach(indexConsumer::accept);
    }

    private void forEachAgentNoiseSlider(BiConsumer<Integer, JSlider> indexSliderConsumer) {
        forEachAgentNoise(index -> indexSliderConsumer.accept(index, agentNoiseSlider.get(index)));
    }

    private void forEachSensorNoiseType(Consumer<SensorNoiseType> sensorNoiseTypeConsumer) {
        Stream.of(RANGE, BEARING, SIGNATURE).forEach(sensorNoiseTypeConsumer::accept);
    }

    private void forEachSensorNoiseTypeSlider(BiConsumer<SensorNoiseType, JSlider> sensorNoiseTypeSliderConsumer) {
        forEachSensorNoiseType(type -> sensorNoiseTypeSliderConsumer.accept(type, sensorNoiseSlider.get(type)));
    }
}
