// Mark McKelvy
// CMPS 523
// Final Project
// File: TestbedView.java
package agv.sim.cmps523;

import static agv.sim.cmps523.GuiUtils.getValueDouble;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JPanel;

public class TestbedView extends JPanel implements Observer {
    static final int size_x = 800, size_y = 400;
    static final PrintStream cout = System.out; // console out
    private static final long serialVersionUID = 1L;
    static double pcm = 1.0 / 2.0; // 1 cm per 2 pixels
    static int cmp = 2; // 2px per 1 cm
    static Ellipse2D m_ellipse = new Ellipse2D.Double();
    static double m_bot_outline_size = 5;
    static double m_subject_bot_outline_size = 5;
    static double m_particle_size = 1.0;
    static double m_particle_orient_size = 10.0;
    boolean m_draw_sensor_beams = true;
    boolean m_draw_sensor_return_beams = true;
    boolean m_draw_object_id = true;

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
        if (m_draw_sensor_beams)
            if (AGVsim.algorithm == 1)
                draw_sensor_scan_wrt_belief(g);
            else if (AGVsim.algorithm == 2)
                draw_sensor_scan_wrt_actual(g);
        if (m_draw_sensor_return_beams)
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
        double cols = AGVsim.m_testbed.get_x_position() * pcm;
        double rows = AGVsim.m_testbed.get_y_position() * pcm; // convert from cm to pixels
        double orient = AGVsim.m_testbed.get_orientation();
        double trans_vel = AGVsim.m_agent.get_translational_velocity() * 5;
        double rot_vel = AGVsim.m_agent.get_rotational_velocity();
        m_ellipse.setFrame(
                x(cols) - m_bot_outline_size,
                y(rows) - m_bot_outline_size,
                (m_bot_outline_size * 2),
                (m_bot_outline_size * 2));
        g2.draw(m_ellipse);

        g2.draw(new Line2D.Double(
                x(cols),
                y(rows),
                x(cols + trans_vel * Math.cos(orient)),
                y(rows + trans_vel * Math.sin(orient))));
        g2.draw(new Line2D.Double(
                x(cols + trans_vel * Math.cos(orient)),
                y(rows + trans_vel * Math.sin(orient)),
                x(cols + trans_vel * Math.cos((orient + rot_vel))),
                y(rows + trans_vel * Math.sin((orient + rot_vel)))));
    }

    // Read the Agent class to draw bot's believed location
    void draw_subjective_bot_outline(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.blue);
        g2.setStroke(new BasicStroke(1));
        double cols = AGVsim.m_agent.get_x_position() * pcm;
        double rows = AGVsim.m_agent.get_y_position() * pcm; // convert from cm to pixels
        double orient = AGVsim.m_agent.get_orientation();
        double trans_vel = AGVsim.m_agent.get_translational_velocity() * 5;
        double rot_vel = AGVsim.m_agent.get_rotational_velocity();
        m_ellipse.setFrame(
                x(cols) - m_subject_bot_outline_size,
                y(rows) - m_subject_bot_outline_size,
                m_subject_bot_outline_size * 2,
                m_subject_bot_outline_size * 2);
        g2.draw(m_ellipse);

        g2.draw(new Line2D.Double(
                x(cols),
                y(rows),
                x(cols + trans_vel * Math.cos(orient)),
                y(rows + trans_vel * Math.sin(orient))));
        g2.draw(new Line2D.Double(
                x(cols + trans_vel * Math.cos(orient)),
                y(rows + trans_vel * Math.sin(orient)),
                x(cols + trans_vel * Math.cos((orient + rot_vel))),
                y(rows + trans_vel * Math.sin((orient + rot_vel)))));
    }

    // Read the Agent class to draw bot's believed location
    void draw_subjective_bot_belief_ellipses(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double cols = AGVsim.m_agent.get_x_position() * pcm;
        double rows = AGVsim.m_agent.get_y_position() * pcm; // convert from cm to pixels
        double angle;
        double[] sqrt_eig = new double[2];

        for (int i = 2; i >= 0; i--) {
            float c = (float) ((2.0 - i) / 3.0);
            g2.setPaint(new Color(c, c, c));

            Matrix covariance_matrix = AGVsim.m_agent.get_covar_mat(i);
            EigenvalueDecomposition eigenvalue_decomposition = covariance_matrix.eig();
            Matrix eigenvector_matrix = eigenvalue_decomposition.getV();
            Matrix eigenvalue_matrix = eigenvalue_decomposition.getD();

            if (i == -1) {
                //cout.println("Covar"); covariance_matrix.print(20, 2);
                cout.println("EVec");
                eigenvector_matrix.print(20, 2);
                cout.println("EVal");
                eigenvalue_matrix.print(20, 2);
            }

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


            if (false)
                cout.println("Ellipse orient: " + Math.toDegrees(angle) + " eigs: " + sqrt_eig[0] + " " + sqrt_eig[1]);
            AffineTransform affine_transform = g2.getTransform();
            m_ellipse.setFrame(
                    -sqrt_eig[0] / 2,
                    -sqrt_eig[1] / 2,
                    sqrt_eig[0],
                    sqrt_eig[1]);
            g2.translate(x(cols), y(rows));
            //g2.rotate(-AGVsim.m_agent.get_orientation());
            g2.rotate(-angle - AGVsim.m_agent.get_orientation());
            g2.draw(m_ellipse);
            //g2.fill(m_ellipse);
            g2.setPaint(Color.black);
            //g2.draw(new Line2D.Double(0, 0, 0, sqrt_eig[1]));
            g2.setTransform(affine_transform);
        }
    }

    void draw_particles(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.m_agent.m_num_particles; i++) {
            Particle p = AGVsim.m_agent.m_particles[i];
            m_ellipse.setFrame(x(p.m_x * pcm) - m_particle_size, y(p.m_y * pcm) - m_particle_size, m_particle_size * 2, m_particle_size * 2);
            g2.draw(m_ellipse);
            //g2.draw(new Line2D.Double(
            //		x(p.m_x*pcm),
            //		y(p.m_y*pcm),
            //		x(p.m_x*pcm+m_particle_orient_size*Math.cos(p.m_angle)),
            //		y(p.m_y*pcm+m_particle_orient_size*Math.sin(p.m_angle))));

        }
    }

    void draw_objects(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < AGVsim.m_testbed.num_objects(); i++) {
            m_ellipse.setFrame(
                    x(AGVsim.m_testbed.object_at(i).m_x * pcm) - AGVsim.m_testbed.object_at(i).m_size,
                    y(AGVsim.m_testbed.object_at(i).m_y * pcm) - AGVsim.m_testbed.object_at(i).m_size,
                    AGVsim.m_testbed.object_at(i).m_size * 2,
                    AGVsim.m_testbed.object_at(i).m_size * 2);
            g2.draw(m_ellipse);
            if (m_draw_object_id)
                g2.drawString(
                        Integer.toString(i),
                        (float) x(AGVsim.m_testbed.object_at(i).m_x * pcm),
                        (float) y(AGVsim.m_testbed.object_at(i).m_y * pcm));
        }
    }

    void draw_sensor_scan_wrt_belief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.m_agent.get_sensor_x_position();
        double y = AGVsim.m_agent.get_sensor_y_position();
        SensorReading[] p = AGVsim.m_agent.m_sensor.get_readings();
        for (int i = 0; i < p.length; i++) {
            if (p[i] != null && p[i].m_actual_range != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(p[i].m_x_believed_hit * pcm),
                        y(p[i].m_y_believed_hit * pcm)));
            }
        }
    }

    void draw_sensor_scan_wrt_actual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.m_agent.m_sensor.get_x_position();
        double y = AGVsim.m_agent.m_sensor.get_y_position();
        SensorReading[] p = AGVsim.m_agent.m_sensor.get_readings();
        for (int i = 0; i < p.length; i++) {
            if (p[i] != null && p[i].m_actual_range != -1) {
                g2.setPaint(Color.green);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(p[i].m_x_actual_hit * pcm),
                        y(p[i].m_y_actual_hit * pcm)));
            }
        }
    }

    void draw_sensor_returns_wrt_belief(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.m_agent.get_sensor_x_position();
        double y = AGVsim.m_agent.get_sensor_y_position();
        Vector v = AGVsim.m_agent.m_sensor.get_hits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = (SensorReading) v.elementAt(i);
            if (p.m_actual_range != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(p.m_x_believed_hit * pcm),
                        y(p.m_y_believed_hit * pcm)));
            }
        }
    }

    void draw_sensor_returns_wrt_actual(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double x = AGVsim.m_agent.m_sensor.get_x_position();
        double y = AGVsim.m_agent.m_sensor.get_y_position();
        Vector v = AGVsim.m_agent.m_sensor.get_hits();
        SensorReading p;
        for (int i = 0; i < v.size(); i++) {
            p = (SensorReading) v.elementAt(i);
            if (p.m_actual_range != -1) {
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(
                        x(x * pcm),
                        y(y * pcm),
                        x(p.m_x_actual_hit * pcm),
                        y(p.m_y_actual_hit * pcm)));
            }
        }
    }

    public void update(Observable o, Object x) {
        repaint();
    }

    public Dimension getMiniumSize() {
        return new Dimension(size_x, size_y);
    }

    public Dimension getPreferredSize() {
        return new Dimension(size_x, size_y);
    }

    private class MouseHandler implements MouseMotionListener, MouseListener {
        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            String mode = (String) ControlPanel.mouse_mode_combo.getSelectedItem();
            if (mode.equals("Add Object")) {
                AGVsim.m_testbed.add_object(
                        x(e.getX()) * cmp,
                        y(e.getY()) * cmp,
                        getValueDouble(ObjectControlPanel.object_size_combo));
            } else if (mode.equals("Bot Actual")) {
                if (Testbed.m_config_orientation) {
                    double x = (x(e.getX()) * cmp - AGVsim.m_testbed.get_initial_x_position());
                    double y = (y(e.getY()) * cmp - AGVsim.m_testbed.get_initial_y_position());
                    AGVsim.m_testbed.set_initial_orientation(Math.atan2(y, x));
                } else {
                    AGVsim.m_testbed.set_initial_position(
                            x(e.getX()) * cmp,
                            y(e.getY()) * cmp);
                }
                Testbed.m_config_orientation = !Testbed.m_config_orientation;
                AGVsim.m_engine.reset_system();
            } else if (mode.equals("Bot Belief")) {
                if (AGVsim.m_agent.m_config_orientation) {
                    double x = (x(e.getX()) * cmp - AGVsim.m_agent.get_initial_x_position());
                    double y = (y(e.getY()) * cmp - AGVsim.m_agent.get_initial_y_position());
                    AGVsim.m_agent.set_initial_orientation(Math.atan2(y, x));
                } else {
                    AGVsim.m_agent.set_initial_position(
                            x(e.getX()) * cmp,
                            y(e.getY()) * cmp);
                }
                AGVsim.m_agent.m_config_orientation = !AGVsim.m_agent.m_config_orientation;
                AGVsim.m_engine.reset_system();
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
