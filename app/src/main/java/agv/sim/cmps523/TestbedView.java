// Mark McKelvy
// CMPS 523
// Final Project
// File: TestbedView.java
package agv.sim.cmps523;

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import agv.sim.cmps523.data.Particle;
import agv.sim.cmps523.data.SensorReading;
import agv.sim.cmps523.data.SimObject;
import agv.sim.cmps523.event.TestbedObserver;
import agv.sim.cmps523.type.ClickMode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Vector;
import javax.swing.JPanel;

public class TestbedView extends JPanel implements TestbedObserver {
    private static final int SIZE_X = 800;
    private static final int SIZE_Y = 400;
    private static final double PCM = 1.0 / 2.0; // 1 cm per 2 pixels
    private static final int CMP = 2; // 2px per 1 cm
    private static final Ellipse2D ELLIPSE = new Ellipse2D.Double();
    private static final double BOT_OUTLINE_SIZE = 5;
    private static final double SUBJECT_BOT_OUTLINE_SIZE = 5;
    private static final double PARTICLE_SIZE = 1.0;
    private final Values values;

    public TestbedView(Values values) {
        this.values = requireNonNull(values);
        MouseHandler m = new MouseHandler();
        addMouseMotionListener(m);
        addMouseListener(m);
        values.addAgentSettingsListener(() -> repaint());
        values.addSensorSettingsListener(() -> repaint());
        values.addSimObjectsListener(() -> repaint());
    }

    public static int getSizeX() {
        return SIZE_X;
    }

    public static int getSizeY() {
        return SIZE_Y;
    }

    private double x(double x) {
        return x;
    }

    private double y(double y) {
        return getHeight() - y;
    }

    private int x(int x) {
        return x;
    }

    private int y(int y) {
        return getHeight() - y;
    }

    @Override
    public void paintComponent(Graphics g) {
        Color savedColor = g.getColor();
        // set color to white and clear the screen
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        switch (values.getAlgorithmType()) {
            case EXTENDED_KALMAN_FILTER -> {
                drawSubjectiveBotBeliefEllipses(g);
                if (values.isSensorBeamsDraw())
                    drawSensorScanWrtBelief(g);
                if (values.isSensorReturnBeamsDraw())
                    drawSensorReturnsWrtBelief(g);
            }
            case MONTE_CARLO_LOCALIZATION -> {
                drawParticles(g);
                if (values.isSensorBeamsDraw())
                    drawSensorScanWrtActual(g);
                if (values.isSensorReturnBeamsDraw())
                    drawSensorReturnsWrtActual(g);
            }
        }
        drawObjects(g);
        drawBotOutline(g);
        drawSubjectiveBotOutline(g);
        drawAxes(g);
        g.setColor(savedColor);
    }

    private void drawAxes(Graphics g) {
        final int x = 800;
        final int y = 400;
        Graphics2D g2 = (Graphics2D) g;

        Font font = new Font("Arial", Font.PLAIN, 8);
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.red);
        g2.draw(new Line2D.Double(x(0), y(0), x(x), y(0)));
        g2.drawString(
                "X", x(x), y(0));
        for (int i = 25; i < x; i += 25) {
            g2.drawString(
                    Integer.toString(i * 2), x(i), y(5));
            g2.draw(new Line2D.Double(x(i), y(0), x(i), y(5)));
        }
        g2.setPaint(Color.blue);
        g2.draw(new Line2D.Double(x(0), y(0), x(0), y(y)));
        g2.drawString(
                "Y", x(0), y(y));
        for (int i = 25; i < y; i += 25) {
            g2.drawString(
                    Integer.toString(i * 2), x(5), y(i));
            g2.draw(new Line2D.Double(x(0), y(i), x(5), y(i)));
        }
    }


    // Read the Testbed class to draw bot's actual location
    private void drawBotOutline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.red);
        g2.setStroke(new BasicStroke(2));
        double cols = Testbed.getCurrent().getXPosition() * PCM;
        double rows = Testbed.getCurrent().getYPosition() * PCM; // convert from cm to pixels
        double orient = Testbed.getCurrent().getOrientation();
        double transVel = values.getAgentTranslationalVelocity() * 5;
        double rotVel = values.getAgentRotationalVelocity();
        final double x1 = x(cols);
        final double y1 = y(rows);
        ELLIPSE.setFrame(
                x1 - BOT_OUTLINE_SIZE,
                y1 - BOT_OUTLINE_SIZE,
                (BOT_OUTLINE_SIZE * 2),
                (BOT_OUTLINE_SIZE * 2));
        g2.draw(ELLIPSE);

        final double x2 = x(cols + transVel * Math.cos(orient));
        final double y2 = y(rows + transVel * Math.sin(orient));
        g2.draw(new Line2D.Double(
                x1,
                y1,
                x2,
                y2));
        final double x3 = x(cols + transVel * Math.cos((orient + rotVel)));
        final double y3 = y(rows + transVel * Math.sin((orient + rotVel)));
        g2.draw(new Line2D.Double(
                x2,
                y2,
                x3,
                y3));
    }

    // Read the Agent class to draw bot's believed location
    private void drawSubjectiveBotOutline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.blue);
        g2.setStroke(new BasicStroke(1));
        double cols = Agent.getCurrent().getXPosition() * PCM;
        double rows = Agent.getCurrent().getYPosition() * PCM; // convert from cm to pixels
        double orient = Agent.getCurrent().getOrientation();
        double transVel = values.getAgentTranslationalVelocity() * 5;
        double rotVel = values.getAgentRotationalVelocity();
        final double x1 = x(cols);
        final double y1 = y(rows);
        ELLIPSE.setFrame(
                x1 - SUBJECT_BOT_OUTLINE_SIZE,
                y1 - SUBJECT_BOT_OUTLINE_SIZE,
                SUBJECT_BOT_OUTLINE_SIZE * 2,
                SUBJECT_BOT_OUTLINE_SIZE * 2);
        g2.draw(ELLIPSE);

        final double x2 = x(cols + transVel * Math.cos(orient));
        final double y2 = y(rows + transVel * Math.sin(orient));
        g2.draw(new Line2D.Double(
                x1,
                y1,
                x2,
                y2));
        final double x3 = x(cols + transVel * Math.cos((orient + rotVel)));
        final double y3 = y(rows + transVel * Math.sin((orient + rotVel)));
        g2.draw(new Line2D.Double(
                x2,
                y2,
                x3,
                y3));
    }

    // Read the Agent class to draw bot's believed location
    private void drawSubjectiveBotBeliefEllipses(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double cols = Agent.getCurrent().getXPosition() * PCM;
        double rows = Agent.getCurrent().getYPosition() * PCM; // convert from cm to pixels
        double angle;
        double[] sqrt_eig = new double[2];

        for (int i = 2; i >= 0; i--) {
            float c = (float) ((2.0 - i) / 3.0);
            g2.setPaint(new Color(c, c, c));

            Matrix covariance_matrix = Agent.getCurrent().getCovarMat(i);
            EigenvalueDecomposition eigenvalue_decomposition = covariance_matrix.eig();
            Matrix eigenvector_matrix = eigenvalue_decomposition.getV();
            Matrix eigenvalue_matrix = eigenvalue_decomposition.getD();

            out.println("Covar");
            covariance_matrix.print(20, 2);
            out.println("EVec");
            eigenvector_matrix.print(20, 2);
            out.println("EVal");
            eigenvalue_matrix.print(20, 2);

            if (eigenvalue_matrix.get(0, 0) >= eigenvalue_matrix.get(1, 1) &&
                    eigenvalue_matrix.get(0, 0) >= eigenvalue_matrix.get(2, 2)) {
                angle = Math.atan2(
                        eigenvector_matrix.get(1, 0),
                        eigenvector_matrix.get(0, 0));
                sqrt_eig[0] = Math.sqrt(eigenvalue_matrix.get(0, 0));
                sqrt_eig[1] = Math.sqrt(eigenvalue_matrix.get(1, 1));
            } else if (eigenvalue_matrix.get(1, 1) >= eigenvalue_matrix.get(0, 0) &&
                    eigenvalue_matrix.get(1, 1) >= eigenvalue_matrix.get(2, 2)) {
                angle = Math.atan2(
                        eigenvector_matrix.get(1, 1),
                        eigenvector_matrix.get(0, 1));
                sqrt_eig[0] = Math.sqrt(eigenvalue_matrix.get(1, 1));
                sqrt_eig[1] = Math.sqrt(eigenvalue_matrix.get(0, 0));
            } else {
                angle = Math.atan2(
                        eigenvector_matrix.get(1, 2),
                        eigenvector_matrix.get(0, 2));
                sqrt_eig[0] = Math.sqrt(eigenvalue_matrix.get(2, 2));
                sqrt_eig[1] = Math.sqrt(eigenvalue_matrix.get(1, 1));
            }


            out.println("Ellipse orient: " + Math.toDegrees(angle) + " eigs: " + sqrt_eig[0] + " " + sqrt_eig[1]);
            AffineTransform affine_transform = g2.getTransform();
            ELLIPSE.setFrame(
                    -sqrt_eig[0] / 2,
                    -sqrt_eig[1] / 2,
                    sqrt_eig[0],
                    sqrt_eig[1]);
            g2.translate(x(cols), y(rows));
            //g2.rotate(-AGVsim.agent.get_orientation());
            g2.rotate(-angle - Agent.getCurrent().getOrientation());
            g2.draw(ELLIPSE);
            //g2.fill(ellipse);
            g2.setPaint(Color.black);
            //g2.draw(new Line2D.Double(0, 0, 0, sqrt_eig[1]));
            g2.setTransform(affine_transform);
        }
    }

    private void drawParticles(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (Particle p : Agent.getCurrent().getParticles()) {
            ELLIPSE.setFrame(x(p.x() * PCM) - PARTICLE_SIZE, y(p.y() * PCM) - PARTICLE_SIZE, PARTICLE_SIZE * 2, PARTICLE_SIZE * 2);
            g2.draw(ELLIPSE);
        }
    }

    private void drawObjects(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        int label = 0;
        for (SimObject simObject : values.getSimObjects()) {
            ELLIPSE.setFrame(
                    x(simObject.x() * PCM) - simObject.size(),
                    y(simObject.y() * PCM) - simObject.size(),
                    simObject.size() * 2,
                    simObject.size() * 2);
            g2.draw(ELLIPSE);
            if (values.isSimObjectsDrawId())
                g2.drawString(
                        Integer.toString(label++),
                        (float) x(simObject.x() * PCM),
                        (float) y(simObject.y() * PCM));
        }
    }

    private void drawSensorScanWrtBelief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = Agent.getCurrent().getSensorXPosition();
        double y = Agent.getCurrent().getSensorYPosition();
        Vector<SensorReading> p = Agent.getCurrent().getSensor().getReadings();
        for (final SensorReading sensorReading : p) {
            if (sensorReading != null && sensorReading.actualRange() != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * PCM),
                        y(y * PCM),
                        x(sensorReading.xBelievedHit() * PCM),
                        y(sensorReading.yBelievedHit() * PCM)));
            }
        }
    }

    private void drawSensorScanWrtActual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = Agent.getCurrent().getSensor().getXPosition();
        double y = Agent.getCurrent().getSensor().getYPosition();
        Vector<SensorReading> p = Agent.getCurrent().getSensor().getReadings();
        for (final SensorReading sensorReading : p) {
            if (sensorReading != null && sensorReading.actualRange() != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * PCM),
                        y(y * PCM),
                        x(sensorReading.xActualHit() * PCM),
                        y(sensorReading.yActualHit() * PCM)));
            }
        }
    }

    private void drawSensorReturnsWrtBelief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = Agent.getCurrent().getSensorXPosition();
        double y = Agent.getCurrent().getSensorYPosition();
        Vector<SensorReading> v = Agent.getCurrent().getSensor().getHits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = v.elementAt(i);
            if (p.actualRange() != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * PCM),
                        y(y * PCM),
                        x(p.xBelievedHit() * PCM),
                        y(p.yBelievedHit() * PCM)));
            }
        }
    }

    private void drawSensorReturnsWrtActual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = Agent.getCurrent().getSensor().getXPosition();
        double y = Agent.getCurrent().getSensor().getYPosition();
        Vector<SensorReading> v = Agent.getCurrent().getSensor().getHits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = v.elementAt(i);
            if (p.actualRange() != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * PCM),
                        y(y * PCM),
                        x(p.xActualHit() * PCM),
                        y(p.yActualHit() * PCM)));
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    @Override
    public void changed() {
        repaint();
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            ClickMode clickMode = values.getClickMode();
            final int xLocation = x(e.getX()) * CMP;
            final int yLocation = y(e.getY()) * CMP;
            switch (clickMode) {
                case ADD_OBJECT -> {
                    final double objectSize = values.getSimObjectSizeChoice();
                    values.addSimObject(new SimObject(xLocation, yLocation, objectSize));
                }
                case BOT_ACTUAL -> {
                    final Testbed testbed = Testbed.getCurrent();
                    if (testbed.isConfigOrientation()) {
                        double x = (xLocation - testbed.getInitialXPosition());
                        double y = (yLocation - testbed.getInitialYPosition());
                        testbed.setInitialOrientation(Math.atan2(y, x));
                    } else {
                        testbed.setInitialPosition(xLocation, yLocation);
                    }
                    testbed.setConfigOrientation(!testbed.isConfigOrientation());
                    values.notifyEngineResetRequested();
                }
                case BOT_BELIEF -> {
                    final Agent agent = Agent.getCurrent();
                    if (agent.isConfigOrientation()) {
                        double x = (xLocation - agent.getInitialXPosition());
                        double y = (yLocation - agent.getInitialYPosition());
                        agent.setInitialOrientation(Math.atan2(y, x));
                    } else {
                        agent.setInitialPosition(
                                xLocation,
                                yLocation);
                    }
                    agent.setConfigOrientation(!agent.isConfigOrientation());
                    values.notifyEngineResetRequested();
                }
            }
        }
    }
}
