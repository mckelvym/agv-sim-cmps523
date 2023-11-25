// Mark McKelvy
// CMPS 523
// Final Project
// File: GuiUtils.java
package agv.sim.cmps523.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Window;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public final class GuiUtils {

    public static void centerOnScreen(Window w) {
        Dimension screenDimension = w.getToolkit().getScreenSize();
        int scrWidth = (int) (screenDimension.getWidth());
        int scrHeight = (int) (screenDimension.getHeight());
        Dimension windowDimension = w.getSize();
        int winWidth = (int) (windowDimension.getWidth());
        int winHeight = (int) (windowDimension.getHeight());
        w.setLocation((scrWidth - winWidth) / 2, (scrHeight - winHeight) / 2);
    }

    public static void addToWindow(Window w, Component c, int posx, int posy, int width, int height) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = width;
        gridBagConstraints.gridheight = height;
        gridBagConstraints.weightx = gridBagConstraints.weighty = 1;
        gridBagConstraints.gridx = posx;
        gridBagConstraints.gridy = posy;
        gridBagConstraints.fill = getFill(c);
        w.add(c, gridBagConstraints);
    }

    public static void addToPanel(JPanel p, Component c, int posx, int posy, int width, int height) {
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
     * @param jComboBox {@link JComboBox}
     * @return selected value cast to string then parsed as double.
     */
    public static double getValueDouble(JComboBox<?> jComboBox) {
        return Double.parseDouble(String.valueOf(jComboBox.getSelectedItem()));
    }

    /**
     * @param jSlider {@link JSlider}
     * @return value cast to double
     */
    public static double getValueDouble(JSlider jSlider) {
        return Integer.valueOf(jSlider.getValue()).doubleValue();
    }
}
