// Mark McKelvy
// CMPS 523
// Final Project
// File: ParticleDialog.java
package agv.sim.cmps523;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

class ParticleDialog extends JDialog{
		private static final long serialVersionUID = 1L;
		static String [] num_particles = {
			"100", "1000", "10000"		
	};
	static JComboBox num_particles_combo = new JComboBox(num_particles);
	JButton close = new JButton("OK");
	static int number_particles;
	
	ParticleDialog(){
		num_particles_combo.setSelectedIndex(0);
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				number_particles = Integer.parseInt(((String)num_particles_combo.getSelectedItem()));
				dispose();
			}
		});
		
		int base_x = 0;
		int base_y = 0;
		this.setLayout(new GridBagLayout());
		
		GuiUtils.add_to_gridbag(this, new JLabel("Choose Number of Particles: "), base_x, base_y, 1, 1);
		GuiUtils.add_to_gridbag(this, num_particles_combo, base_x, base_y+1, 1, 1);
		GuiUtils.add_to_gridbag(this, close, base_x, base_y+2, 1, 1);
		
		this.pack();
		GuiUtils.center_on_screen(this);
		this.setVisible(true);
	}	
}