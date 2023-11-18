// Mark McKelvy
// CMPS 523
// Final Project
// File: ObjectControlPanel.java
package agv.sim.cmps523;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.*;

public class ObjectControlPanel extends JDialog {
    static final int sizeX = 500, sizeY = 250;
    static final JButton OBJECT_REMOVE_BUTTON = new JButton("Remove");
    static JTextField xLocField = new JTextField(5);
    static JTextField yLocField = new JTextField(5);
    static JButton addButton = new JButton("Add");
    static JButton closeButton = new JButton("Close");
    static String[] objectSize = {
            "1"
    };
    static JComboBox<String> objectSizeCombo = new JComboBox<>(objectSize);
    static JComboBox<String> objectCombo = new JComboBox<>();
    static String[] objectIdDisplay = {
            "Yes", "No"
    };
    static JComboBox<String> objectIdDisplayCombo = new JComboBox<>(objectIdDisplay);

    ObjectControlPanel() {
        closeButton.addActionListener(e -> dispose());
        addButton.addActionListener(new ObjectAddHandler());
        objectSizeCombo.addActionListener(new ObjectSizeHandler());
        OBJECT_REMOVE_BUTTON.addActionListener(new ObjectRemoveButtonHandler());
        objectIdDisplayCombo.addActionListener(new ObjectIdDisplayHandler());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());


        GuiUtils.addToGridbag(this, new JLabel("Add Point"), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("X Location:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, xLocField, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Y Location:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, yLocField, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Size:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, objectSizeCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, addButton, base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Remove Points"), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.addToGridbag(this, OBJECT_REMOVE_BUTTON, base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, objectCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Display Object IDs:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, objectIdDisplayCombo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, closeButton, base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    public Dimension getMinimumSize() {
        return new Dimension(sizeX, sizeY);
    }

    public Dimension getPreferredSize() {
        return new Dimension(sizeX, sizeY);
    }

    private static class ObjectAddHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int x = Integer.parseInt(xLocField.getText());
            int y = Integer.parseInt(yLocField.getText());
            AGVsim.testbed.addObject(x, y,
                    Double.parseDouble(String.valueOf(objectSizeCombo.getSelectedItem())));
        }
    }

    private static class ObjectSizeHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        }
    }

    private static class ObjectRemoveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (objectCombo.getItemCount() > 0) {
                int obj_id = objectCombo.getSelectedIndex();
                objectCombo.removeItemAt(obj_id);
                AGVsim.testbed.removeObject(obj_id + 1);
            }
        }
    }

    private static class ObjectIdDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
            AGVsim.testbedview.drawObjectId = Objects.equals(disp, "Yes");
            AGVsim.testbedview.repaint();
        }
    }
}
