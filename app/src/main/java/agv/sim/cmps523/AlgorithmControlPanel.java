// Mark McKelvy
// CMPS 523
// Final Project
// File: AlgorithmControlPanel.java
package agv.sim.cmps523;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class AlgorithmControlPanel extends JDialog
{
	private static final long serialVersionUID = 1L;
	static String [] bayesian_filter = {
		"None", "Extended Kalman Filter (EKF)", "Monte Carlo Localization (MCL)"		
	};
	static JComboBox bayesian_filter_combo = new JComboBox(bayesian_filter);
	JButton close = new JButton("OK");
	
	AlgorithmControlPanel(){
		bayesian_filter_combo.setSelectedIndex(0);
		bayesian_filter_combo.addActionListener(new AlgorithmChoiceHandler());
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (!((String)bayesian_filter_combo.getSelectedItem()).equals("None"))
					dispose();
			}
		});
		
		int base_x = 0;
		int base_y = 0;
		this.setLayout(new GridBagLayout());
		
		GuiUtils.add_to_gridbag(this, new JLabel("Choose Bayesian Filter: "), base_x, base_y, 1, 1);
		GuiUtils.add_to_gridbag(this, bayesian_filter_combo, base_x, base_y+1, 1, 1);
		GuiUtils.add_to_gridbag(this, close, base_x, base_y+2, 1, 1);

		this.pack();
		GuiUtils.center_on_screen(this);
		this.setVisible(true);
	}
	
	
	private class AlgorithmChoiceHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int choice = ((JComboBox)e.getSource()).getSelectedIndex();
			if (choice > 0){
				AGVsim.algorithm = choice;
				if (choice > 1)
					new ParticleDialog();
			}			
		}
	}	
}
