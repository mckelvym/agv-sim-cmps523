package agv.sim.cmps523.event;

import java.util.function.Consumer;

public class JComboBoxSelectedDoubleListener extends JComboBoxSelectedStringListener {
    public JComboBoxSelectedDoubleListener(Consumer<Double> selectedItemConsumer) {
        super(string -> selectedItemConsumer.accept(Double.parseDouble(string)));
    }
}
