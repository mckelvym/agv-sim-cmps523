// Mark McKelvy
// CMPS 523
// Final Project
// File: GuiUtils.java
package agv.sim.cmps523;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Window;
import javax.swing.JPanel;

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
        GridBagConstraints gbconstr = new GridBagConstraints();
        gbconstr.gridwidth = width;
        gbconstr.gridheight = height;
        gbconstr.weightx = gbconstr.weighty = 1;
        gbconstr.gridx = posx;
        gbconstr.gridy = posy;
        w.add(c, gbconstr);
    }

    static public void add_to_gridbag(JPanel p, Component c, int posx, int posy, int width, int height) {
        GridBagConstraints gbconstr = new GridBagConstraints();
        gbconstr.gridwidth = width;
        gbconstr.gridheight = height;
        gbconstr.weightx = gbconstr.weighty = 1;
        gbconstr.gridx = posx;
        gbconstr.gridy = posy;
        p.add(c, gbconstr);
    }
}
