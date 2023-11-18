// Mark McKelvy
// CMPS 523
// Final Project
// File: AGVsim.java
package agv.sim.cmps523;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class AGVsim extends JFrame {
    static final ControlPanel control_panel = new ControlPanel();
    static Logger logger = new Logger();
    static Sensor sensor = new Sensor();
    static Testbed testbed = new Testbed(); // actual world
    static Agent agent = new Agent();   // subjective model
    static TestbedView testbedview = new TestbedView();
    static Engine engine = new Engine();
    static int algorithm = 0;
    static boolean started = false;

    public AGVsim(String title) {
        super(title);

        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
                logger.save_data();
                System.exit(0);
            }

            public void windowActivated(WindowEvent arg0) {
            }

            public void windowClosed(WindowEvent arg0) {
            }

            public void windowDeactivated(WindowEvent arg0) {
            }

            public void windowDeiconified(WindowEvent arg0) {
            }

            public void windowIconified(WindowEvent arg0) {
            }

            public void windowOpened(WindowEvent arg0) {
            }
        });

        Container cp;

        // add menubar
        setupMenuBar();

        // set size of window
        int size_x = ControlPanel.size_x +
                TestbedView.size_x;
        int size_y = ControlPanel.size_y +
                TestbedView.size_y;
        setSize(size_x, size_y);                  // see dims in TestbedView.java

        // add components to window
        cp = getContentPane();  // set the panel container
        cp.setLayout(new BorderLayout()); // use border layout
        cp.add("North", control_panel);    // put Control in north panel
        cp.add("Center", testbedview);
        //setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        pack();

        GuiUtils.center_on_screen(this);
        // display
        setResizable(false);    // fixed size display
        setVisible(true);
        //new NoiseControlPanel();
    }

    public static void main(String[] args) {
        AlgorithmControlPanel acp = new AlgorithmControlPanel();
        acp.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
            }

            public void windowActivated(WindowEvent arg0) {
            }

            public void windowClosed(WindowEvent arg0) {
                if (!started) {
                    started = true;
                    out.println("Starting up..");
                    new AGVsim("AGV Sim");
                    engine.build_architecture();
                }
            }

            public void windowDeactivated(WindowEvent arg0) {
            }

            public void windowDeiconified(WindowEvent arg0) {
            }

            public void windowIconified(WindowEvent arg0) {
            }

            public void windowOpened(WindowEvent arg0) {
            }
        });

        testbed.add_object_without_repaint(600, 700, 1);
        testbed.add_object_without_repaint(700, 200, 1);
        testbed.add_object_without_repaint(100, 180, 1);
    }

    private void setupMenuBar() {
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

        menu_options_exit.addActionListener(e -> System.exit(0));

        menu_objects_configure.addActionListener(e -> new ObjectControlPanel());

        menu_sensor_configure.addActionListener(e -> new SensorControlPanel());
        menu_noise_configure.addActionListener(e -> new NoiseControlPanel());
    }
}
