package agv.sim.cmps523.event;

import static java.util.Objects.requireNonNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.JComboBox;

public class JComboBoxSelectionIndexListener implements ActionListener {

    private final Consumer<Integer> selectionIndexConsumer;

    public JComboBoxSelectionIndexListener(Consumer<Integer> selectionIndexConsumer) {
        this.selectionIndexConsumer = requireNonNull(selectionIndexConsumer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source instanceof JComboBox<?> combo) {
            final int index = combo.getSelectedIndex();
            selectionIndexConsumer.accept(index);
            System.out.println("selectedIndex=" + index);
        }
    }
}
