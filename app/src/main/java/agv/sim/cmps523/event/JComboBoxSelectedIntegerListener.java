package agv.sim.cmps523.event;

import java.util.function.Consumer;

public class JComboBoxSelectedIntegerListener extends JComboBoxSelectedStringListener {
    public JComboBoxSelectedIntegerListener(Consumer<Integer> selectedItemConsumer) {
        super(string -> selectedItemConsumer.accept(Integer.parseInt(string)));
    }
}
