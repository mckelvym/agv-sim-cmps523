package agv.sim.cmps523.event;

import java.util.function.Consumer;

public class JSliderPercentValueListener extends JSliderDoubleValueListener {
    public JSliderPercentValueListener(Consumer<Double> valueConsumer) {
        super(value -> valueConsumer.accept(value / 100.0));
    }
}
