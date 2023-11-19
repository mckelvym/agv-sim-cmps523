package agv.sim.cmps523.event;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JSliderDoubleValueListener implements ChangeListener {
    private final Consumer<Double> valueConsumer;

    public JSliderDoubleValueListener(Consumer<Double> valueConsumer) {
        this.valueConsumer = requireNonNull(valueConsumer);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final Object source = e.getSource();
        if (source instanceof JSlider slider) {
            final double value = Integer.valueOf(slider.getValue()).doubleValue();
            valueConsumer.accept(value);
            System.out.println("new-value=" + value);
        }
    }
}
