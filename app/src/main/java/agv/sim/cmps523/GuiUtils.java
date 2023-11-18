// Mark McKelvy
// CMPS 523
// Final Project
// File: GuiUtils.java
package agv.sim.cmps523;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Window;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public final class GuiUtils {

    static public void center_on_screen(Window w) {
        int scr_width, scr_height, win_width, win_height;
        Dimension dim;
        dim = w.getToolkit().getScreenSize();
        scr_width = (int) (dim.getWidth());
        scr_height = (int) (dim.getHeight());
        dim = w.getSize();
        win_width = (int) (dim.getWidth());
        win_height = (int) (dim.getHeight());
        w.setLocation((scr_width - win_width) / 2, (scr_height - win_height) / 2);
    }

    static public void add_to_gridbag(Window w, Component c, int posx, int posy, int width, int height) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = width;
        gridBagConstraints.gridheight = height;
        gridBagConstraints.weightx = gridBagConstraints.weighty = 1;
        gridBagConstraints.gridx = posx;
        gridBagConstraints.gridy = posy;
        gridBagConstraints.fill = getFill(c);
        w.add(c, gridBagConstraints);
    }

    static public void add_to_gridbag(JPanel p, Component c, int posx, int posy, int width, int height) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = width;
        gridBagConstraints.gridheight = height;
        gridBagConstraints.weightx = gridBagConstraints.weighty = 1;
        gridBagConstraints.gridx = posx;
        gridBagConstraints.gridy = posy;
        gridBagConstraints.fill = getFill(c);
        p.add(c, gridBagConstraints);
    }

    private static int getFill(Component c) {
        if (c instanceof JTextField || c instanceof JSlider) {
            return GridBagConstraints.HORIZONTAL;
        }
        return GridBagConstraints.NONE;
    }

    /**
     * @param jComboBox
     * @return selected value cast to string then parsed as double.
     */
    public static double getValueDouble(JComboBox jComboBox) {
        return Double.valueOf(String.valueOf(jComboBox.getSelectedItem()));
    }

    /**
     * @param jSlider
     * @return value cast to double
     */
    public static double getValueDouble(JSlider jSlider) {
        return Integer.valueOf(jSlider.getValue()).doubleValue();
    }
}
