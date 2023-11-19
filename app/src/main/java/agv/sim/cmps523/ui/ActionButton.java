package agv.sim.cmps523.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ActionButton extends AbstractAction {
    private final Runnable onClick;

    public ActionButton(String text, Runnable onClick) {
        super(text);
        this.onClick = onClick;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onClick.run();
    }
}
