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
    static final int sizeX = 800, sizeY = 400;
    static double pcm = 1.0 / 2.0; // 1 cm per 2 pixels
    static int cmp = 2; // 2px per 1 cm
    static Ellipse2D ellipse = new Ellipse2D.Double();
    static double botOutlineSize = 5;
    static double subjectBotOutlineSize = 5;
    static double particleSize = 1.0;
    boolean drawSensorBeams = true;
    boolean drawSensorReturnBeams = true;
    boolean drawObjectId = true;

    public TestbedView() {
        MouseHandler m = new MouseHandler();
        this.addMouseMotionListener(m);
        this.addMouseListener(m);
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
        if (AGVsim.algorithm == 1)
            drawSubjectiveBotBeliefEllipses(g);
        if (AGVsim.algorithm == 2)
            drawParticles(g);
        if (drawSensorBeams)
            if (AGVsim.algorithm == 1)
                drawSensorScanWrtBelief(g);
            else if (AGVsim.algorithm == 2)
                drawSensorScanWrtActual(g);
        if (drawSensorReturnBeams)
            if (AGVsim.algorithm == 1)
                drawSensorReturnsWrtBelief(g);
            else if (AGVsim.algorithm == 2)
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
        double cols = AGVsim.testbed.getXPosition() * pcm;
        double rows = AGVsim.testbed.getYPosition() * pcm; // convert from cm to pixels
        double orient = AGVsim.testbed.getOrientation();
        double trans_vel = AGVsim.agent.getTranslationalVelocity() * 5;
        double rot_vel = AGVsim.agent.getRotationalVelocity();
        final double x1 = x(cols);
        final double y1 = y(rows);
        ellipse.setFrame(
                x1 - botOutlineSize,
                y1 - botOutlineSize,
                (botOutlineSize * 2),
                (botOutlineSize * 2));
        g2.draw(ellipse);

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
        double cols = AGVsim.agent.getXPosition() * pcm;
        double rows = AGVsim.agent.getYPosition() * pcm; // convert from cm to pixels
        double orient = AGVsim.agent.getOrientation();
        double trans_vel = AGVsim.agent.getTranslationalVelocity() * 5;
        double rot_vel = AGVsim.agent.getRotationalVelocity();
        final double x1 = x(cols);
        final double y1 = y(rows);
        ellipse.setFrame(
                x1 - subjectBotOutlineSize,
                y1 - subjectBotOutlineSize,
                subjectBotOutlineSize * 2,
                subjectBotOutlineSize * 2);
        g2.draw(ellipse);

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
        double cols = AGVsim.agent.getXPosition() * pcm;
        double rows = AGVsim.agent.getYPosition() * pcm; // convert from cm to pixels
        double angle;
        double[] sqrt_eig = new double[2];

        for (int i = 2; i >= 0; i--) {
            float c = (float) ((2.0 - i) / 3.0);
            g2.setPaint(new Color(c, c, c));

            Matrix covariance_matrix = AGVsim.agent.getCovarMat(i);
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
            ellipse.setFrame(
                    -sqrt_eig[0] / 2,
                    -sqrt_eig[1] / 2,
                    sqrt_eig[0],
                    sqrt_eig[1]);
            g2.translate(x(cols), y(rows));
            //g2.rotate(-AGVsim.agent.get_orientation());
            g2.rotate(-angle - AGVsim.agent.getOrientation());
            g2.draw(ellipse);
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
        for (int i = 0; i < AGVsim.agent.numParticles; i++) {
            Particle p = AGVsim.agent.particles[i];
            ellipse.setFrame(x(p.x * pcm) - particleSize, y(p.y * pcm) - particleSize, particleSize * 2, particleSize * 2);
            g2.draw(ellipse);
        }
    }

    void drawObjects(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.testbed.numObjects(); i++) {
            ellipse.setFrame(
                    x(AGVsim.testbed.objectAt(i).x * pcm) - AGVsim.testbed.objectAt(i).size,
                    y(AGVsim.testbed.objectAt(i).y * pcm) - AGVsim.testbed.objectAt(i).size,
                    AGVsim.testbed.objectAt(i).size * 2,
                    AGVsim.testbed.objectAt(i).size * 2);
            g2.draw(ellipse);
            if (drawObjectId)
                g2.drawString(
                        Integer.toString(i),
                        (float) x(AGVsim.testbed.objectAt(i).x * pcm),
                        (float) y(AGVsim.testbed.objectAt(i).y * pcm));
        }
    }

    void drawSensorScanWrtBelief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.agent.getSensorXPosition();
        double y = AGVsim.agent.getSensorYPosition();
        SensorReading[] p = AGVsim.agent.sensor.get_readings();
        for (final SensorReading sensorReading : p) {
            if (sensorReading != null && sensorReading.actualRange != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(sensorReading.xBelievedHit * pcm),
                        y(sensorReading.yBelievedHit * pcm)));
            }
        }
    }

    void drawSensorScanWrtActual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.agent.sensor.get_x_position();
        double y = AGVsim.agent.sensor.get_y_position();
        SensorReading[] p = AGVsim.agent.sensor.get_readings();
        for (final SensorReading sensorReading : p) {
            if (sensorReading != null && sensorReading.actualRange != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(sensorReading.xActualHit * pcm),
                        y(sensorReading.yActualHit * pcm)));
            }
        }
    }

    void drawSensorReturnsWrtBelief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.agent.getSensorXPosition();
        double y = AGVsim.agent.getSensorYPosition();
        Vector<SensorReading> v = AGVsim.agent.sensor.get_hits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = v.elementAt(i);
            if (p.actualRange != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(p.xBelievedHit * pcm),
                        y(p.yBelievedHit * pcm)));
            }
        }
    }

    void drawSensorReturnsWrtActual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.agent.sensor.get_x_position();
        double y = AGVsim.agent.sensor.get_y_position();
        Vector<SensorReading> v = AGVsim.agent.sensor.get_hits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = v.elementAt(i);
            if (p.actualRange != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(p.xActualHit * pcm),
                        y(p.yActualHit * pcm)));
            }
        }
    }

    public void update(Observable o, Object x) {
        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension(sizeX, sizeY);
    }

    private class MouseHandler implements MouseMotionListener, MouseListener {
        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            String mode = (String) ControlPanel.mouseModeCombo.getSelectedItem();
            if (Objects.equals(mode, "Add Object")) {
                AGVsim.testbed.addObject(
                        x(e.getX()) * cmp,
                        y(e.getY()) * cmp,
                        getValueDouble(ObjectControlPanel.objectSizeCombo));
            } else if (Objects.equals(mode, "Bot Actual")) {
                if (Testbed.configOrientation) {
                    double x = (x(e.getX()) * cmp - AGVsim.testbed.getInitialXPosition());
                    double y = (y(e.getY()) * cmp - AGVsim.testbed.getInitialYPosition());
                    AGVsim.testbed.setInitialOrientation(Math.atan2(y, x));
                } else {
                    AGVsim.testbed.setInitialPosition(
                            x(e.getX()) * cmp,
                            y(e.getY()) * cmp);
                }
                Testbed.configOrientation = !Testbed.configOrientation;
                AGVsim.engine.resetSystem();
            } else if (Objects.equals(mode, "Bot Belief")) {
                if (AGVsim.agent.configOrientation) {
                    double x = (x(e.getX()) * cmp - AGVsim.agent.getInitialXPosition());
                    double y = (y(e.getY()) * cmp - AGVsim.agent.getInitialYPosition());
                    AGVsim.agent.setInitialOrientation(Math.atan2(y, x));
                } else {
                    AGVsim.agent.setInitialPosition(
                            x(e.getX()) * cmp,
                            y(e.getY()) * cmp);
                }
                AGVsim.agent.configOrientation = !AGVsim.agent.configOrientation;
                AGVsim.engine.resetSystem();
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
