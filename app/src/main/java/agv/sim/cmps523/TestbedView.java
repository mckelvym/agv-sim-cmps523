// Mark McKelvy
// CMPS 523
// Final Project
// File: TestbedView.java
package agv.sim.cmps523;

import static agv.sim.cmps523.GuiUtils.getValueDouble;
import static java.lang.System.out;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JPanel;

public class TestbedView extends JPanel implements Observer {
    private static final int sizeX = 800;
    private static final int sizeY = 400;
    private static double pcm = 1.0 / 2.0; // 1 cm per 2 pixels
    private static int cmp = 2; // 2px per 1 cm
    private static Ellipse2D ellipse = new Ellipse2D.Double();
    private static double botOutlineSize = 5;
    private static double subjectBotOutlineSize = 5;
    private static double particleSize = 1.0;
    private boolean drawSensorBeams = true;
    private boolean drawSensorReturnBeams = true;
    private boolean drawObjectId = true;

    public TestbedView() {
        MouseHandler m = new MouseHandler();
        this.addMouseMotionListener(m);
        this.addMouseListener(m);
    }

    public static int getSizeX() {
        return sizeX;
    }

    public static int getSizeY() {
        return sizeY;
    }

    public static double getPcm() {
        return pcm;
    }

    public static void setPcm(double pcm) {
        TestbedView.pcm = pcm;
    }

    public static int getCmp() {
        return cmp;
    }

    public static void setCmp(int cmp) {
        TestbedView.cmp = cmp;
    }

    public static Ellipse2D getEllipse() {
        return ellipse;
    }

    public static void setEllipse(Ellipse2D ellipse) {
        TestbedView.ellipse = ellipse;
    }

    public static double getBotOutlineSize() {
        return botOutlineSize;
    }

    public static void setBotOutlineSize(double botOutlineSize) {
        TestbedView.botOutlineSize = botOutlineSize;
    }

    public static double getSubjectBotOutlineSize() {
        return subjectBotOutlineSize;
    }

    public static void setSubjectBotOutlineSize(double subjectBotOutlineSize) {
        TestbedView.subjectBotOutlineSize = subjectBotOutlineSize;
    }

    public static double getParticleSize() {
        return particleSize;
    }

    public static void setParticleSize(double particleSize) {
        TestbedView.particleSize = particleSize;
    }

    double x(double x) {
        return x;
    }

    double y(double y) {
        return getHeight() - y;
    }

    int x(int x) {
        return x;
    }

    int y(int y) {
        return getHeight() - y;
    }

    public void paintComponent(Graphics g) {
        Color savedColor = g.getColor();
        // set color to white and clear the screen
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (AGVsim.getAlgorithm() == 1)
            drawSubjectiveBotBeliefEllipses(g);
        if (AGVsim.getAlgorithm() == 2)
            drawParticles(g);
        if (isDrawSensorBeams())
            if (AGVsim.getAlgorithm() == 1)
                drawSensorScanWrtBelief(g);
            else if (AGVsim.getAlgorithm() == 2)
                drawSensorScanWrtActual(g);
        if (isDrawSensorReturnBeams())
            if (AGVsim.getAlgorithm() == 1)
                drawSensorReturnsWrtBelief(g);
            else if (AGVsim.getAlgorithm() == 2)
                drawSensorReturnsWrtActual(g);
        drawObjects(g);
        drawBotOutline(g);
        //if (AGVsim.algorithm == 1)
        drawSubjectiveBotOutline(g);
        drawAxes(g);
        g.setColor(savedColor);
    }

    void drawAxes(Graphics g) {
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
    void drawBotOutline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.red);
        g2.setStroke(new BasicStroke(2));
        double cols = AGVsim.getTestbed().getXPosition() * getPcm();
        double rows = AGVsim.getTestbed().getYPosition() * getPcm(); // convert from cm to pixels
        double orient = AGVsim.getTestbed().getOrientation();
        double trans_vel = AGVsim.getAgent().getTranslationalVelocity() * 5;
        double rot_vel = AGVsim.getAgent().getRotationalVelocity();
        final double x1 = x(cols);
        final double y1 = y(rows);
        getEllipse().setFrame(
                x1 - getBotOutlineSize(),
                y1 - getBotOutlineSize(),
                (getBotOutlineSize() * 2),
                (getBotOutlineSize() * 2));
        g2.draw(getEllipse());

        final double x2 = x(cols + trans_vel * Math.cos(orient));
        final double y2 = y(rows + trans_vel * Math.sin(orient));
        g2.draw(new Line2D.Double(
                x1,
                y1,
                x2,
                y2));
        final double x3 = x(cols + trans_vel * Math.cos((orient + rot_vel)));
        final double y3 = y(rows + trans_vel * Math.sin((orient + rot_vel)));
        g2.draw(new Line2D.Double(
                x2,
                y2,
                x3,
                y3));
    }

    // Read the Agent class to draw bot's believed location
    void drawSubjectiveBotOutline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.blue);
        g2.setStroke(new BasicStroke(1));
        double cols = AGVsim.getAgent().getXPosition() * getPcm();
        double rows = AGVsim.getAgent().getYPosition() * getPcm(); // convert from cm to pixels
        double orient = AGVsim.getAgent().getOrientation();
        double trans_vel = AGVsim.getAgent().getTranslationalVelocity() * 5;
        double rot_vel = AGVsim.getAgent().getRotationalVelocity();
        final double x1 = x(cols);
        final double y1 = y(rows);
        getEllipse().setFrame(
                x1 - getSubjectBotOutlineSize(),
                y1 - getSubjectBotOutlineSize(),
                getSubjectBotOutlineSize() * 2,
                getSubjectBotOutlineSize() * 2);
        g2.draw(getEllipse());

        final double x2 = x(cols + trans_vel * Math.cos(orient));
        final double y2 = y(rows + trans_vel * Math.sin(orient));
        g2.draw(new Line2D.Double(
                x1,
                y1,
                x2,
                y2));
        final double x3 = x(cols + trans_vel * Math.cos((orient + rot_vel)));
        final double y3 = y(rows + trans_vel * Math.sin((orient + rot_vel)));
        g2.draw(new Line2D.Double(
                x2,
                y2,
                x3,
                y3));
    }

    // Read the Agent class to draw bot's believed location
    void drawSubjectiveBotBeliefEllipses(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double cols = AGVsim.getAgent().getXPosition() * getPcm();
        double rows = AGVsim.getAgent().getYPosition() * getPcm(); // convert from cm to pixels
        double angle;
        double[] sqrt_eig = new double[2];

        for (int i = 2; i >= 0; i--) {
            float c = (float) ((2.0 - i) / 3.0);
            g2.setPaint(new Color(c, c, c));

            Matrix covariance_matrix = AGVsim.getAgent().getCovarMat(i);
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
            getEllipse().setFrame(
                    -sqrt_eig[0] / 2,
                    -sqrt_eig[1] / 2,
                    sqrt_eig[0],
                    sqrt_eig[1]);
            g2.translate(x(cols), y(rows));
            //g2.rotate(-AGVsim.agent.get_orientation());
            g2.rotate(-angle - AGVsim.getAgent().getOrientation());
            g2.draw(getEllipse());
            //g2.fill(ellipse);
            g2.setPaint(Color.black);
            //g2.draw(new Line2D.Double(0, 0, 0, sqrt_eig[1]));
            g2.setTransform(affine_transform);
        }
    }

    void drawParticles(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.getAgent().getNumParticles(); i++) {
            Particle p = AGVsim.getAgent().getParticles()[i];
            getEllipse().setFrame(x(p.getX() * getPcm()) - getParticleSize(), y(p.getY() * getPcm()) - getParticleSize(), getParticleSize() * 2, getParticleSize() * 2);
            g2.draw(getEllipse());
        }
    }

    void drawObjects(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.getTestbed().numObjects(); i++) {
            getEllipse().setFrame(
                    x(AGVsim.getTestbed().objectAt(i).getX() * getPcm()) - AGVsim.getTestbed().objectAt(i).getSize(),
                    y(AGVsim.getTestbed().objectAt(i).getY() * getPcm()) - AGVsim.getTestbed().objectAt(i).getSize(),
                    AGVsim.getTestbed().objectAt(i).getSize() * 2,
                    AGVsim.getTestbed().objectAt(i).getSize() * 2);
            g2.draw(getEllipse());
            if (isDrawObjectId())
                g2.drawString(
                        Integer.toString(i),
                        (float) x(AGVsim.getTestbed().objectAt(i).getX() * getPcm()),
                        (float) y(AGVsim.getTestbed().objectAt(i).getY() * getPcm()));
        }
    }

    void drawSensorScanWrtBelief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.getAgent().getSensorXPosition();
        double y = AGVsim.getAgent().getSensorYPosition();
        SensorReading[] p = AGVsim.getAgent().getSensor().get_readings();
        for (final SensorReading sensorReading : p) {
            if (sensorReading != null && sensorReading.getActualRange() != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * getPcm()),
                        y(y * getPcm()),
                        x(sensorReading.getxBelievedHit() * getPcm()),
                        y(sensorReading.getyBelievedHit() * getPcm())));
            }
        }
    }

    void drawSensorScanWrtActual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.getAgent().getSensor().get_x_position();
        double y = AGVsim.getAgent().getSensor().get_y_position();
        SensorReading[] p = AGVsim.getAgent().getSensor().get_readings();
        for (final SensorReading sensorReading : p) {
            if (sensorReading != null && sensorReading.getActualRange() != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * getPcm()),
                        y(y * getPcm()),
                        x(sensorReading.getxActualHit() * getPcm()),
                        y(sensorReading.getyActualHit() * getPcm())));
            }
        }
    }

    void drawSensorReturnsWrtBelief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.getAgent().getSensorXPosition();
        double y = AGVsim.getAgent().getSensorYPosition();
        Vector<SensorReading> v = AGVsim.getAgent().getSensor().get_hits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = v.elementAt(i);
            if (p.getActualRange() != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * getPcm()),
                        y(y * getPcm()),
                        x(p.getxBelievedHit() * getPcm()),
                        y(p.getyBelievedHit() * getPcm())));
            }
        }
    }

    void drawSensorReturnsWrtActual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.getAgent().getSensor().get_x_position();
        double y = AGVsim.getAgent().getSensor().get_y_position();
        Vector<SensorReading> v = AGVsim.getAgent().getSensor().get_hits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = v.elementAt(i);
            if (p.getActualRange() != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * getPcm()),
                        y(y * getPcm()),
                        x(p.getxActualHit() * getPcm()),
                        y(p.getyActualHit() * getPcm())));
            }
        }
    }

    public void update(Observable o, Object x) {
        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension(getSizeX(), getSizeY());
    }

    public boolean isDrawSensorBeams() {
        return drawSensorBeams;
    }

    public void setDrawSensorBeams(boolean drawSensorBeams) {
        this.drawSensorBeams = drawSensorBeams;
    }

    public boolean isDrawSensorReturnBeams() {
        return drawSensorReturnBeams;
    }

    public void setDrawSensorReturnBeams(boolean drawSensorReturnBeams) {
        this.drawSensorReturnBeams = drawSensorReturnBeams;
    }

    public boolean isDrawObjectId() {
        return drawObjectId;
    }

    public void setDrawObjectId(boolean drawObjectId) {
        this.drawObjectId = drawObjectId;
    }

    private class MouseHandler implements MouseMotionListener, MouseListener {
        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            String mode = (String) ControlPanel.getMouseModeCombo().getSelectedItem();
            if (Objects.equals(mode, "Add Object")) {
                AGVsim.getTestbed().addObject(
                        x(e.getX()) * getCmp(),
                        y(e.getY()) * getCmp(),
                        getValueDouble(ObjectControlPanel.getObjectSizeCombo()));
            } else if (Objects.equals(mode, "Bot Actual")) {
                if (Testbed.isConfigOrientation()) {
                    double x = (x(e.getX()) * getCmp() - AGVsim.getTestbed().getInitialXPosition());
                    double y = (y(e.getY()) * getCmp() - AGVsim.getTestbed().getInitialYPosition());
                    AGVsim.getTestbed().setInitialOrientation(Math.atan2(y, x));
                } else {
                    AGVsim.getTestbed().setInitialPosition(
                            x(e.getX()) * getCmp(),
                            y(e.getY()) * getCmp());
                }
                Testbed.setConfigOrientation(!Testbed.isConfigOrientation());
                AGVsim.getEngine().resetSystem();
            } else if (Objects.equals(mode, "Bot Belief")) {
                if (AGVsim.getAgent().isConfigOrientation()) {
                    double x = (x(e.getX()) * getCmp() - AGVsim.getAgent().getInitialXPosition());
                    double y = (y(e.getY()) * getCmp() - AGVsim.getAgent().getInitialYPosition());
                    AGVsim.getAgent().setInitialOrientation(Math.atan2(y, x));
                } else {
                    AGVsim.getAgent().setInitialPosition(
                            x(e.getX()) * getCmp(),
                            y(e.getY()) * getCmp());
                }
                AGVsim.getAgent().setConfigOrientation(!AGVsim.getAgent().isConfigOrientation());
                AGVsim.getEngine().resetSystem();
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }
}
