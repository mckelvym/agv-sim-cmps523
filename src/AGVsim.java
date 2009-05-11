// Mark McKelvy
// CMPS 523
// Final Project
// File: AGVsim.java

import java.io.*;
import java.awt.*;     // abstract window toolkit
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;  // swing classes

public class AGVsim extends JFrame {
	private static final long serialVersionUID = 1L;
	static final PrintStream cout = System.out; // console out
	static final ControlPanel m_control_panel = new ControlPanel();
	static		 Logger m_logger = new Logger();
	static		 Sensor m_sensor = new Sensor();
	static       Testbed m_testbed = new Testbed(); // actual world
	static       Agent m_agent = new Agent();   // subjective model
	static       TestbedView m_testbedview = new TestbedView();
	static       Engine m_engine = new Engine();
	static		 int algorithm = 0;
	static 		 boolean started = false;
	
	public static void main (String args[]) {
		AlgorithmControlPanel acp = new AlgorithmControlPanel();
		acp.addWindowListener(new WindowListener(){
			public void windowClosing(WindowEvent e){}
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {
				if (!started){
					started = true;
					cout.println("Starting up..");
					new AGVsim("AGV Sim");
					m_engine.build_architecture();
				}
			}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		
		m_testbed.add_object_without_repaint(600, 700, 1);
		m_testbed.add_object_without_repaint(700, 200, 1);
		m_testbed.add_object_without_repaint(100, 180, 1);
	}

	public AGVsim (String title) {
		super (title);
		
		this.addWindowListener(new WindowListener(){
			public void windowClosing(WindowEvent e){
				m_logger.save_data();
				System.exit(0);
			}
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		
		Container cp;

		// add menubar
		setupMenuBar();

		// set size of window
		int size_x = ControlPanel.size_x +
					TestbedView.size_x;
		int size_y = ControlPanel.size_y +
					TestbedView.size_y;
		setSize (size_x, size_y);		          // see dims in TestbedView.java

		// add components to window
		cp = getContentPane ();  // set the panel container
		cp.setLayout (new BorderLayout ()); // use border layout
		cp.add ("North", m_control_panel);    // put Control in north panel
		cp.add ("Center", m_testbedview);
		//setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		pack ();

		GuiUtils.center_on_screen(this);
		// display
		setResizable (false);    // fixed size display
		setVisible (true);
		//new NoiseControlPanel();
	}
	
	private void setupMenuBar ()
	{
		JMenuBar menu_bar = new JMenuBar();

		JMenuItem menu_objects_configure = new JMenuItem("Configure Objects");
		JMenuItem menu_sensor_configure = new JMenuItem("Configure Sensor");
		JMenuItem menu_noise_configure = new JMenuItem("Configure Noise");
		JMenuItem menu_options_exit = new JMenuItem("Exit");
		
		menu_bar.add((new JMenu("Exit")).add(menu_options_exit));
		menu_bar.add((new JMenu("Configure Objects")).add(menu_objects_configure));
		menu_bar.add((new JMenu("Configure Sensor")).add(menu_sensor_configure));
		menu_bar.add((new JMenu("Configure Noise")).add(menu_noise_configure));
		
		this.setJMenuBar(menu_bar);

		menu_options_exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		menu_objects_configure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new ObjectControlPanel();
			}
		});

		menu_sensor_configure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new SensorControlPanel();
			}
		});
		menu_noise_configure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new NoiseControlPanel();
			}
		});
	}
}
