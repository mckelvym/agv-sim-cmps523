// Mark McKelvy
// CMPS 523
// Final Project
// File: ObjectControlPanel.java
package agv.sim.cmps523.ui;

import static agv.sim.cmps523.ui.GuiUtils.addToWindow;
import static agv.sim.cmps523.ui.GuiUtils.centerOnScreen;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.Values;
import agv.sim.cmps523.data.SimObject;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Objects;
import javax.swing.*;

public class ObjectControlPanel extends JDialog {
    private static final int SIZE_X = 500;
    private static final int SIZE_Y = 250;
    private final JComboBox<String> objectSizeCombo;
    private final JComboBox<String> objectCombo;
    private final JComboBox<String> objectIdDisplayCombo;
    private final JButton objectRemoveButton;
    private final JTextField xLocField;
    private final JTextField yLocField;
    private final JButton addButton;
    private final JButton closeButton;
    private final Values values;

    public ObjectControlPanel(Values values) {
        this.values = requireNonNull(values);
        xLocField = new JTextField(5);
        yLocField = new JTextField(5);
        addButton = new JButton("Add");
        objectRemoveButton = new JButton("Remove");
        objectIdDisplayCombo = new JComboBox<>(new String[]{"Yes", "No"});
        objectCombo = new JComboBox<>();
        values.getSimObjects().stream().map(String::valueOf).forEach(objectCombo::addItem);
        objectSizeCombo = new JComboBox<>(new String[]{String.valueOf(values.getSimObjectSizeChoice())});
        closeButton = new JButton("Close");
    }

    public void display() {
        addButton.addActionListener(e -> {
            int x = parseInt(xLocField.getText());
            int y = parseInt(yLocField.getText());
            final double size = parseDouble(String.valueOf(objectSizeCombo.getSelectedItem()));
            final SimObject simObject = new SimObject(x, y, size);
            values.addSimObject(simObject);
            objectCombo.addItem(String.valueOf(simObject));
        });
        objectRemoveButton.addActionListener(e -> {
            if (!values.getSimObjects().isEmpty()) {
                int selectedIndex = objectCombo.getSelectedIndex();
                objectCombo.removeItemAt(selectedIndex);
                values.removeSimObject(selectedIndex);
            }
        });
        objectIdDisplayCombo.addActionListener(e -> {
            String disp = String.valueOf(((JComboBox<?>) e.getSource()).getSelectedItem());
            final boolean isSimObjectsDrawId = Objects.equals(disp, "Yes");
            values.setSimObjectsDrawId(isSimObjectsDrawId);
        });
        closeButton.addActionListener(e -> dispose());

        int baseX = 0;
        int baseY = 0;
        setLayout(new GridBagLayout());

        addToWindow(this, new JLabel("Add Point"), baseX, baseY, 2, 1);
        baseY++;
        addToWindow(this, new JLabel("X Location:"), baseX, baseY, 1, 1);
        addToWindow(this, xLocField, baseX + 1, baseY, 1, 1);
        baseY++;
        addToWindow(this, new JLabel("Y Location:"), baseX, baseY, 1, 1);
        addToWindow(this, yLocField, baseX + 1, baseY, 1, 1);
        baseY++;
        addToWindow(this, new JLabel("Size:"), baseX, baseY, 1, 1);
        addComboToWindow(objectSizeCombo, baseX + 1, baseY);
        baseY++;
        addToWindow(this, addButton, baseX, baseY, 1, 1);
        addToWindow(this, new JLabel(""), baseX + 1, baseY, 1, 1);
        baseY++;
        addToWindow(this, objectRemoveButton, baseX, baseY, 1, 1);
        addComboToWindow(objectCombo, baseX + 1, baseY);
        baseY++;
        addToWindow(this, new JLabel("Display Object IDs:"), baseX, baseY, 1, 1);
        addComboToWindow(objectIdDisplayCombo, baseX + 1, baseY);
        baseY++;
        addToWindow(this, closeButton, baseX, baseY, 2, 1);

        pack();
        centerOnScreen(this);
        setVisible(true);
    }

    private void addComboToWindow(JComboBox<?> combo, int baseX, int baseY) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = gridBagConstraints.weighty = 1;
        gridBagConstraints.gridx = baseX;
        gridBagConstraints.gridy = baseY;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(combo, gridBagConstraints);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(SIZE_X, SIZE_Y);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE_X, SIZE_Y);
    }
}
