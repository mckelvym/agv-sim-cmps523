// Mark McKelvy
// CMPS 523
// Final Project
// File: ObjectControlPanel.java
package agv.sim.cmps523;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.*;

public class ObjectControlPanel extends JDialog {
    static final int size_x = 500, size_y = 250;
    static final PrintStream cout = System.out; // console out
    static final JButton object_remove_button = new JButton("Remove");
    private static final long serialVersionUID = 1L;
    static JTextField x_loc_field = new JTextField(5);
    static JTextField y_loc_field = new JTextField(5);
    static JButton add_button = new JButton("Add");
    static JButton close_button = new JButton("Close");
    static String[] object_size = {
            "1"
    };
    static JComboBox<String> object_size_combo = new JComboBox<>(object_size);
    static JComboBox<String> object_combo = new JComboBox<>();
    static String[] object_id_display = {
            "Yes", "No"
    };
    static JComboBox<String> object_id_display_combo = new JComboBox<>(object_id_display);

    ObjectControlPanel() {
        close_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add_button.addActionListener(new ObjectAddHandler());
        object_size_combo.addActionListener(new ObjectSizeHandler());
        object_remove_button.addActionListener(new ObjectRemoveButtonHandler());
        object_id_display_combo.addActionListener(new ObjectIdDisplayHandler());

        int base_x = 0;
        int base_y = 0;
        this.setLayout(new GridBagLayout());


        GuiUtils.add_to_gridbag(this, new JLabel("Add Point"), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("X Location:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, x_loc_field, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Y Location:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, y_loc_field, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Size:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, object_size_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, add_button, base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Remove Points"), base_x, base_y, 2, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, object_remove_button, base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, object_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, new JLabel("Display Object IDs:"), base_x, base_y, 1, 1);
        GuiUtils.add_to_gridbag(this, object_id_display_combo, base_x + 1, base_y, 1, 1);
        base_y++;
        GuiUtils.add_to_gridbag(this, close_button, base_x, base_y, 2, 1);

        this.pack();
        GuiUtils.center_on_screen(this);
        this.setVisible(true);
    }

    public Dimension getMinimumSize() {
        return new Dimension(size_x, size_y);
    }

    public Dimension getPreferredSize() {
        return new Dimension(size_x, size_y);
    }

    private class ObjectAddHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int x = (Integer.valueOf(x_loc_field.getText())).intValue();
            int y = (Integer.valueOf(y_loc_field.getText())).intValue();
            AGVsim.m_testbed.add_object(x, y,
                    Double.valueOf(String.valueOf(object_size_combo.getSelectedItem())));
            //x_loc_field.setText("");
            //y_loc_field.setText("");
        }
    }

    private class ObjectSizeHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        }
    }

    private class ObjectRemoveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (object_combo.getItemCount() > 0) {
                int obj_id = object_combo.getSelectedIndex();
                object_combo.removeItemAt(obj_id);
                AGVsim.m_testbed.remove_object(obj_id + 1);
            }
        }
    }

    private class ObjectIdDisplayHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String disp = (String) ((JComboBox) e.getSource()).getSelectedItem();
            AGVsim.m_testbedview.m_draw_object_id = disp.equals("Yes");
            AGVsim.m_testbedview.repaint();
        }
    }
}
