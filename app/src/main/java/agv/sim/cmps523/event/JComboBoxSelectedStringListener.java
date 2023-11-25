package agv.sim.cmps523.event;

import static java.util.Objects.requireNonNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.JComboBox;

public class JComboBoxSelectedStringListener implements ActionListener {

    private final Consumer<String> selectedItemConsumer;

    public JComboBoxSelectedStringListener(Consumer<String> selectedItemConsumer) {
        this.selectedItemConsumer = requireNonNull(selectedItemConsumer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source instanceof JComboBox<?> combo) {
            final String item = String.valueOf(combo.getSelectedItem());
            selectedItemConsumer.accept(item);
            System.out.println("selectedItem=" + item);
        }
    }
}
