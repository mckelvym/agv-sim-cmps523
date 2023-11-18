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
    private static final ControlPanel controlPanel = new ControlPanel();
    private static Logger logger = new Logger();
    private static Sensor sensor = new Sensor();
    private static Testbed testbed = new Testbed(); // actual world
    private static Agent agent = new Agent();   // subjective model
    private static TestbedView testbedview = new TestbedView();
    private static Engine engine = new Engine();
    private static int algorithm = 0;
    private static boolean started = false;

    public AGVsim(String title) {
        super(title);

        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
                getLogger().saveData();
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
        int size_x = ControlPanel.getSizeX() +
                TestbedView.getSizeX();
        int size_y = ControlPanel.getSizeY() +
                TestbedView.getSizeY();
        setSize(size_x, size_y);                  // see dims in TestbedView.java

        // add components to window
        cp = getContentPane();  // set the panel container
        cp.setLayout(new BorderLayout()); // use border layout
        cp.add("North", getControlPanel());    // put Control in north panel
        cp.add("Center", getTestbedview());
        //setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        pack();

        GuiUtils.centerOnScreen(this);
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
                if (!isStarted()) {
                    setStarted(true);
                    out.println("Starting up..");
                    new AGVsim("AGV Sim");
                    getEngine().buildArchitecture();
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

        getTestbed().addObjectWithoutRepaint(600, 700, 1);
        getTestbed().addObjectWithoutRepaint(700, 200, 1);
        getTestbed().addObjectWithoutRepaint(100, 180, 1);
    }

    public static ControlPanel getControlPanel() {
        return controlPanel;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        AGVsim.logger = logger;
    }

    public static Sensor getSensor() {
        return sensor;
    }

    public static void setSensor(Sensor sensor) {
        AGVsim.sensor = sensor;
    }

    public static Testbed getTestbed() {
        return testbed;
    }

    public static void setTestbed(Testbed testbed) {
        AGVsim.testbed = testbed;
    }

    public static Agent getAgent() {
        return agent;
    }

    public static void setAgent(Agent agent) {
        AGVsim.agent = agent;
    }

    public static TestbedView getTestbedview() {
        return testbedview;
    }

    public static void setTestbedview(TestbedView testbedview) {
        AGVsim.testbedview = testbedview;
    }

    public static Engine getEngine() {
        return engine;
    }

    public static void setEngine(Engine engine) {
        AGVsim.engine = engine;
    }

    public static int getAlgorithm() {
        return algorithm;
    }

    public static void setAlgorithm(int algorithm) {
        AGVsim.algorithm = algorithm;
    }

    public static boolean isStarted() {
        return started;
    }

    public static void setStarted(boolean started) {
        AGVsim.started = started;
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
