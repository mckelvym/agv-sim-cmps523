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
    private static final int sizeX = 500;
    private static final int sizeY = 250;
    private static final JButton OBJECT_REMOVE_BUTTON = new JButton("Remove");
    private static JTextField xLocField = new JTextField(5);
    private static JTextField yLocField = new JTextField(5);
    private static JButton addButton = new JButton("Add");
    private static JButton closeButton = new JButton("Close");
    private static String[] objectSize = {
            "1"
    };
    private static JComboBox<String> objectSizeCombo = new JComboBox<>(getObjectSize());
    private static JComboBox<String> objectCombo = new JComboBox<>();
    private static String[] objectIdDisplay = {
            "Yes", "No"
    };
    private static JComboBox<String> objectIdDisplayCombo = new JComboBox<>(getObjectIdDisplay());

    ObjectControlPanel() {
        getCloseButton().addActionListener(e -> dispose());
        getAddButton().addActionListener(new ObjectAddHandler());
        getObjectSizeCombo().addActionListener(new ObjectSizeHandler());
        getObjectRemoveButton().addActionListener(new ObjectRemoveButtonHandler());
        getObjectIdDisplayCombo().addActionListener(new ObjectIdDisplayHandler());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());


        GuiUtils.addToGridbag(this, new JLabel("Add Point"), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("X Location:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getxLocField(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Y Location:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getyLocField(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Size:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getObjectSizeCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, getAddButton(), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Remove Points"), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.addToGridbag(this, getObjectRemoveButton(), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getObjectCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, new JLabel("Display Object IDs:"), base_x, base_y, 1, 1);
        GuiUtils.addToGridbag(this, getObjectIdDisplayCombo(), base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.addToGridbag(this, getCloseButton(), base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.centerOnScreen(this);
        this.setVisible(true);
    }

    public static int getSizeX() {
        return sizeX;
    }

    public static int getSizeY() {
        return sizeY;
    }

    public static JButton getObjectRemoveButton() {
        return OBJECT_REMOVE_BUTTON;
    }

    public static JTextField getxLocField() {
        return xLocField;
    }

    public static void setxLocField(JTextField xLocField) {
        ObjectControlPanel.xLocField = xLocField;
    }

    public static JTextField getyLocField() {
        return yLocField;
    }

    public static void setyLocField(JTextField yLocField) {
        ObjectControlPanel.yLocField = yLocField;
    }

    public static JButton getAddButton() {
        return addButton;
    }

    public static void setAddButton(JButton addButton) {
        ObjectControlPanel.addButton = addButton;
    }

    public static JButton getCloseButton() {
        return closeButton;
    }

    public static void setCloseButton(JButton closeButton) {
        ObjectControlPanel.closeButton = closeButton;
    }

    public static String[] getObjectSize() {
        return objectSize;
    }

    public static void setObjectSize(String[] objectSize) {
        ObjectControlPanel.objectSize = objectSize;
    }

    public static JComboBox<String> getObjectSizeCombo() {
        return objectSizeCombo;
    }

    public static void setObjectSizeCombo(JComboBox<String> objectSizeCombo) {
        ObjectControlPanel.objectSizeCombo = objectSizeCombo;
    }

    public static JComboBox<String> getObjectCombo() {
        return objectCombo;
    }

    public static void setObjectCombo(JComboBox<String> objectCombo) {
        ObjectControlPanel.objectCombo = objectCombo;
    }

    public static String[] getObjectIdDisplay() {
        return objectIdDisplay;
    }

    public static void setObjectIdDisplay(String[] objectIdDisplay) {
        ObjectControlPanel.objectIdDisplay = objectIdDisplay;
    }

    public static JComboBox<String> getObjectIdDisplayCombo() {
        return objectIdDisplayCombo;
    }

    public static void setObjectIdDisplayCombo(JComboBox<String> objectIdDisplayCombo) {
        ObjectControlPanel.objectIdDisplayCombo = objectIdDisplayCombo;
    }

    public Dimension getMinimumSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    public Dimension getPreferredSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    private static class ObjectAddHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int x = Integer.parseInt(getxLocField().getText());
            int y = Integer.parseInt(getyLocField().getText());
            AGVsim.getTestbed().addObject(x, y,
                    Double.parseDouble(String.valueOf(getObjectSizeCombo().getSelectedItem())));
        }
    }

    private static class ObjectSizeHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        }
    }

    private static class ObjectRemoveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (getObjectCombo().getItemCount() > 0) {
                int obj_id = getObjectCombo().getSelectedIndex();
                getObjectCombo().removeItemAt(obj_id);
                AGVsim.getTestbed().removeObject(obj_id + 1);
            }
        }
    }

    private static class ObjectIdDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
            AGVsim.getTestbedview().setDrawObjectId(Objects.equals(disp, "Yes"));
            AGVsim.getTestbedview().repaint();
        }
    }
}
