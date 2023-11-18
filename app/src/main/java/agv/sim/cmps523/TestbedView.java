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
            draw_subjective_bot_belief_ellipses(g);
        if (AGVsim.algorithm == 2)
            draw_particles(g);
        if (drawSensorBeams)
            if (AGVsim.algorithm == 1)
                draw_sensor_scan_wrt_belief(g);
            else if (AGVsim.algorithm == 2)
                draw_sensor_scan_wrt_actual(g);
        if (drawSensorReturnBeams)
            if (AGVsim.algorithm == 1)
                draw_sensor_returns_wrt_belief(g);
            else if (AGVsim.algorithm == 2)
                draw_sensor_returns_wrt_actual(g);
        draw_objects(g);
        draw_bot_outline(g);
        //if (AGVsim.algorithm == 1)
        draw_subjective_bot_outline(g);
        draw_axes(g);
        g.setColor(savedColor);
    }

    void draw_axes(Graphics g) {
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
    void draw_bot_outline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.red);
        g2.setStroke(new BasicStroke(2));
        double cols = AGVsim.testbed.get_x_position() * pcm;
        double rows = AGVsim.testbed.get_y_position() * pcm; // convert from cm to pixels
        double orient = AGVsim.testbed.get_orientation();
        double trans_vel = AGVsim.agent.get_translational_velocity() * 5;
        double rot_vel = AGVsim.agent.get_rotational_velocity();
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
    void draw_subjective_bot_outline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.blue);
        g2.setStroke(new BasicStroke(1));
        double cols = AGVsim.agent.get_x_position() * pcm;
        double rows = AGVsim.agent.get_y_position() * pcm; // convert from cm to pixels
        double orient = AGVsim.agent.get_orientation();
        double trans_vel = AGVsim.agent.get_translational_velocity() * 5;
        double rot_vel = AGVsim.agent.get_rotational_velocity();
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
    void draw_subjective_bot_belief_ellipses(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double cols = AGVsim.agent.get_x_position() * pcm;
        double rows = AGVsim.agent.get_y_position() * pcm; // convert from cm to pixels
        double angle;
        double[] sqrt_eig = new double[2];

        for (int i = 2; i >= 0; i--) {
            float c = (float) ((2.0 - i) / 3.0);
            g2.setPaint(new Color(c, c, c));

            Matrix covariance_matrix = AGVsim.agent.get_covar_mat(i);
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
            g2.rotate(-angle - AGVsim.agent.get_orientation());
            g2.draw(ellipse);
            //g2.fill(ellipse);
            g2.setPaint(Color.black);
            //g2.draw(new Line2D.Double(0, 0, 0, sqrt_eig[1]));
            g2.setTransform(affine_transform);
        }
    }

    void draw_particles(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.agent.numParticles; i++) {
            Particle p = AGVsim.agent.particles[i];
            ellipse.setFrame(x(p.x * pcm) - particleSize, y(p.y * pcm) - particleSize, particleSize * 2, particleSize * 2);
            g2.draw(ellipse);
        }
    }

    void draw_objects(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.testbed.nuobjects(); i++) {
            ellipse.setFrame(
                    x(AGVsim.testbed.object_at(i).x * pcm) - AGVsim.testbed.object_at(i).size,
                    y(AGVsim.testbed.object_at(i).y * pcm) - AGVsim.testbed.object_at(i).size,
                    AGVsim.testbed.object_at(i).size * 2,
                    AGVsim.testbed.object_at(i).size * 2);
            g2.draw(ellipse);
            if (drawObjectId)
                g2.drawString(
                        Integer.toString(i),
                        (float) x(AGVsim.testbed.object_at(i).x * pcm),
                        (float) y(AGVsim.testbed.object_at(i).y * pcm));
        }
    }

    void draw_sensor_scan_wrt_belief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.agent.get_sensor_x_position();
        double y = AGVsim.agent.get_sensor_y_position();
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

    void draw_sensor_scan_wrt_actual(Graphics g) {
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

    void draw_sensor_returns_wrt_belief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.agent.get_sensor_x_position();
        double y = AGVsim.agent.get_sensor_y_position();
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

    void draw_sensor_returns_wrt_actual(Graphics g) {
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
                AGVsim.testbed.add_object(
                        x(e.getX()) * cmp,
                        y(e.getY()) * cmp,
                        getValueDouble(ObjectControlPanel.objectSizeCombo));
            } else if (Objects.equals(mode, "Bot Actual")) {
                if (Testbed.configOrientation) {
                    double x = (x(e.getX()) * cmp - AGVsim.testbed.get_initial_x_position());
                    double y = (y(e.getY()) * cmp - AGVsim.testbed.get_initial_y_position());
                    AGVsim.testbed.set_initial_orientation(Math.atan2(y, x));
                } else {
                    AGVsim.testbed.set_initial_position(
                            x(e.getX()) * cmp,
                            y(e.getY()) * cmp);
                }
                Testbed.configOrientation = !Testbed.configOrientation;
                AGVsim.engine.reset_system();
            } else if (Objects.equals(mode, "Bot Belief")) {
                if (AGVsim.agent.configOrientation) {
                    double x = (x(e.getX()) * cmp - AGVsim.agent.get_initial_x_position());
                    double y = (y(e.getY()) * cmp - AGVsim.agent.get_initial_y_position());
                    AGVsim.agent.set_initial_orientation(Math.atan2(y, x));
                } else {
                    AGVsim.agent.set_initial_position(
                            x(e.getX()) * cmp,
                            y(e.getY()) * cmp);
                }
                AGVsim.agent.configOrientation = !AGVsim.agent.configOrientation;
                AGVsim.engine.reset_system();
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
