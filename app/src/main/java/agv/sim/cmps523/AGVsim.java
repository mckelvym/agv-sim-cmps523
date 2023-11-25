// Mark McKelvy
// CMPS 523
// Final Project
// File: AGVsim.java
package agv.sim.cmps523;

import static agv.sim.cmps523.ui.GuiUtils.centerOnScreen;
import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.data.Logger;
import agv.sim.cmps523.type.AlgorithmType;
import agv.sim.cmps523.ui.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class AGVsim extends JFrame {
    private final Values values;

    public AGVsim(String title, Values values, Logger logger) {
        super(title);
        this.values = requireNonNull(values);
        requireNonNull(logger);

        final ControlPanel controlPanel = new ControlPanel(values);
        final TestbedView testbedView = new TestbedView(values);

        values.addTestbedObserver(testbedView);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.saveData();
                System.exit(0);
            }
        });

        // add menubar
        setupMenuBar();

        // set size of window
        int sizeX = ControlPanel.getSizeX() +
                TestbedView.getSizeX();
        int sizeY = ControlPanel.getSizeY() +
                TestbedView.getSizeY();
        setSize(sizeX, sizeY);                  // see dims in TestbedView.java

        // add components to window
        Container cp = getContentPane();  // set the panel container
        cp.setLayout(new BorderLayout()); // use border layout
        cp.add("North", controlPanel);    // put Control in north panel
        cp.add("Center", testbedView);

        //setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        pack();

        centerOnScreen(this);
        // display
        setResizable(false);    // fixed size display
        setVisible(true);
    }

    public static void main(String[] args) {
        Values values = new Values();
        Logger logger = new Logger(values);
        Sensor sensor = new Sensor(values);
        Engine engine = new Engine(values, logger);
        Agent.newAgent(values, logger).setSensor(sensor);
        Testbed.newTestbed(values, logger);

        Runnable startAgvSim = getAgvSimStarter(values, logger, engine);

        AlgorithmControlPanel acp = new AlgorithmControlPanel(values);
        acp.addWindowListener(handleAlgorithmChoice(values, startAgvSim));
        acp.display();
    }

    private static Runnable getAgvSimStarter(Values values, Logger logger, Engine engine) {
        return () -> {
            out.println("Starting up..");
            new AGVsim("AGV Sim", values, logger);
            engine.buildRequested();
        };
    }

    private static WindowAdapter handleAlgorithmChoice(Values values, Runnable startAgvSim) {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (requireNonNull(values.getAlgorithmType()) == AlgorithmType.NONE) {
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
                System.out.println(values.getAlgorithmType());
                switch (values.getAlgorithmType()) {
                    case NONE -> System.exit(0);
                    case EXTENDED_KALMAN_FILTER -> startAgvSim.run();
                    case MONTE_CARLO_LOCALIZATION -> promptParticlesThenStart(values, startAgvSim);
                }
            }
        };
    }

    private static void promptParticlesThenStart(Values values, Runnable startAgvSim) {
        final ParticleDialog particleDialog = new ParticleDialog(values);
        particleDialog.display();
        particleDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                startAgvSim.run();
            }
        });
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem menuObjectsConfigure = new JMenuItem("Configure Objects");
        JMenuItem menuSensorConfigure = new JMenuItem("Configure Sensor");
        JMenuItem menuNoiseConfigure = new JMenuItem("Configure Noise");
        JMenuItem menuOptionsExit = new JMenuItem("Exit");

        menuBar.add((new JMenu("Exit")).add(menuOptionsExit));
        menuBar.add((new JMenu("Configure Objects")).add(menuObjectsConfigure));
        menuBar.add((new JMenu("Configure Sensor")).add(menuSensorConfigure));
        menuBar.add((new JMenu("Configure Noise")).add(menuNoiseConfigure));

        setJMenuBar(menuBar);

        menuOptionsExit.addActionListener(e -> System.exit(0));

        menuObjectsConfigure.addActionListener(e -> new ObjectControlPanel(values).display());

        menuSensorConfigure.addActionListener(e -> new SensorControlPanel(values).display());
        menuNoiseConfigure.addActionListener(e -> new NoiseControlPanel(values).display());
    }
}
