// Mark McKelvy
// CMPS 523
// Final Project
// File: Agent.java 
package agv.sim.cmps523;

import static java.lang.System.out;

import Jama.Matrix;
import java.util.Vector;

public class Agent {
    private final Matrix control;
    Sensor sensor;
    boolean enableCorrection;
    int numParticles = 100;
    Particle[] particles;
    // agent noise
    double a1Noise = 0.0; // default value
    double a2Noise = 0.0; // default value
    double a3Noise = 0.0; // default value
    double a4Noise = 0.0; // default value
    double a5Noise = 0.0; // default value
    double a6Noise = 0.0; // default value
    Matrix Qt;
    boolean configOrientation = false;
    private Matrix pose;
    private Matrix sensorPose;
    private Matrix SIGMA;
    private Matrix lhsSIGMABar, rhsSIGMABar;
    private double subjectiveBotLocationX0 = 180; // initial subjective bot loc x
    private double subjectiveBotLocationY0 = 200; // initial subjective bot loc y
    private double subjectiveBotOrientation0 = Math.PI / 2;

    public Agent() {
        pose = new Matrix(3, 1);
        sensorPose = new Matrix(3, 1);
        control = new Matrix(2, 1);
        SIGMA = new Matrix(3, 3);
        lhsSIGMABar = rhsSIGMABar = SIGMA;
        Qt = new Matrix(3, 3);
        initialize_subjective_bot_pose();
        initialize_agent_noise();
        initialize_particles();
        enableCorrection = true;
    }

    void initialize_particles() {
        numParticles = ParticleDialog.numberParticles;
        particles = new Particle[numParticles];
        out.println("Initting " + numParticles + " particles.");
        for (int i = 0; i < numParticles; i++) {
            particles[i] = new Particle(
                    Utils.gaussian(subjectiveBotLocationX0, 1),
                    Utils.gaussian(subjectiveBotLocationY0, 1),
                    Utils.gaussian(subjectiveBotOrientation0, Math.toRadians(1))
            );
        }
    }

    void initialize_agent_noise() {
        a1Noise = NoiseControlPanel.get_alpha_noise(1);
        a2Noise = NoiseControlPanel.get_alpha_noise(2);
        a3Noise = NoiseControlPanel.get_alpha_noise(3);
        a4Noise = NoiseControlPanel.get_alpha_noise(4);
        for (int i = 0; i <= 2; i++) {
            Qt.set(i, i, NoiseControlPanel.get_sensor_noise(i + 1));
            String index;
            if (i == 0)
                index = "range";
            else if (i == 1)
                index = "bearing";
            else
                index = "signature";
            out.println("Agent: Qt noise sigma[" + index + "]^2 = " + Qt.get(i, i));
        }
        if (AGVsim.algorithm == 1) {
            out.println("Agent: alpha noise: " + a1Noise + " " + a2Noise + " " + a3Noise + " " + a4Noise);
        } else {
            a5Noise = NoiseControlPanel.get_alpha_noise(5);
            a6Noise = NoiseControlPanel.get_alpha_noise(6);
            out.println("Agent: alpha noise: " + a1Noise + " " + a2Noise + " " + a3Noise + " " + a4Noise + " " + a5Noise + " " + a6Noise);
        }
    }

    void initialize_subjective_bot_pose() {
        set_position(subjectiveBotLocationX0, subjectiveBotLocationY0);
        set_orientation(subjectiveBotOrientation0);
        control.set(0, 0, 0);
        control.set(1, 0, 0);
        SIGMA.set(0, 0, 0.0);
        SIGMA.set(0, 1, 0.0);
        SIGMA.set(0, 2, 0.0);
        SIGMA.set(1, 0, 0.0);
        SIGMA.set(1, 1, 0.0);
        SIGMA.set(1, 2, 0.0);
        SIGMA.set(2, 0, 0.0);
        SIGMA.set(2, 1, 0.0);
        SIGMA.set(2, 2, 0.0);

        AGVsim.logger.save_agent_pose(pose);
    }

    void ekf() {
        AGVsim.testbed.move(control);

        double dt = Engine.deltaT;        // delta time
        double v = (control.get(0, 0));            // v from robot motion - translational velocity
        double w = (control.get(1, 0));            // omega from robot motion - rotataional velocity
        double v_over_w = (w != 0.0) ? v / w : 0;
        double w_sqr = w * w;
        double theta = get_orientation(); // LINE 2
        double dtheta = w * dt;
        double theta_dtheta = theta + dtheta;
        double cos_theta = Math.cos(theta);
        double sin_theta = Math.sin(theta);
        double cos_theta_dtheta = Math.cos(theta_dtheta);
        double sin_theta_dtheta = Math.sin(theta_dtheta);

        Matrix SIGMAt_bar;
        Matrix Mut_update;
        Matrix Mut_bar;
        Matrix Gt;
        Matrix GtT;
        Matrix Vt;
        Matrix VtT;
        Matrix Mt;

        // LINE 3
        // Jacobian for linearized motion model wrt to pose (x,y,theta) taken of g (motion model)
        // taken at u_t and mu_{t-1}
        Gt = Matrix.identity(3, 3);
        Gt.set(0, 2, v_over_w * (-cos_theta + cos_theta_dtheta));
        Gt.set(1, 2, v_over_w * (-sin_theta + sin_theta_dtheta));
        GtT = Gt.transpose();

        // LINE 4
        // Jacobian for linearized motion model wrt to control (v,w) taken of g (motion model)
        // taken at u_t and mu_{t-1}
        Vt = new Matrix(3, 2);
        // column 1
        Vt.set(0, 0, -(sin_theta + sin_theta_dtheta) / w);
        Vt.set(1, 0, (cos_theta - cos_theta_dtheta) / w);
        Vt.set(2, 0, 0);
        // column 2
        Vt.set(0, 1,
                (v * (sin_theta - sin_theta_dtheta)) / w_sqr +
                        (v * cos_theta_dtheta * dt) / w
        );
        Vt.set(1, 1,
                -(v * (cos_theta - cos_theta_dtheta)) / w_sqr +
                        (v * sin_theta_dtheta * dt) / w
        );
        Vt.set(2, 1, dt);
        VtT = Vt.transpose();

        // LINE 5, motion noise covariance matrix in control space
        Mt = Matrix.identity(2, 2);
        out.println("Agent: alpha noise: " + a1Noise + " " + a2Noise + " " + a3Noise + " " + a4Noise);
        Mt.set(0, 0, Utils.square(a1Noise * v + a2Noise * w));
        Mt.set(1, 1, Utils.square(a3Noise * v + a4Noise * w));

        // LINE 6, motion update from motion model
        Mut_update = new Matrix(3, 1);
        Mut_update.set(0, 0, -(v_over_w * sin_theta) + (v_over_w * sin_theta_dtheta));
        Mut_update.set(1, 0, (v_over_w * cos_theta) - (v_over_w * cos_theta_dtheta));
        Mut_update.set(2, 0, dtheta);

        Mut_bar = pose;
        Mut_bar = Mut_bar.plus(Mut_update);
        Mut_bar.set(2, 0, Utils.clamp_angle(Mut_bar.get(2, 0)));
        out.println("Believed Pose: " + Mut_bar.get(0, 0) + " " + Mut_bar.get(1, 0) + " " + Math.toDegrees(Mut_bar.get(2, 0)));

        // LINE 7, motion covariance matrix update from motion model
        // lhs of plus: predicted belief based on Jacobian control actions and SIGMA
        // rhs of plus: mapping between motion noise in control space to motion noise in state space.
        lhsSIGMABar = Gt.times(SIGMA.times(GtT));
        rhsSIGMABar = Vt.times(Mt.times(VtT));

        SIGMAt_bar = lhsSIGMABar.plus(rhsSIGMABar);

        sensorPose = Mut_bar;
        if (sensor != null)
            sensor.sense(AGVsim.testbed);

        // LINE 8, covariance matrix for error in laser range finder
        // setup in function initialize_agent_noise();

        if (sensor != null && enableCorrection) {
            out.println("Correction Step");

            // LINE 9, for each actual observation...
            SensorReading z_t_i;
            Vector<SensorReading> z_t = sensor.get_hits();
            for (int i = 0; i < z_t.size(); i++) {
                out.println("Processing hit..");

                // LINE 10, unique identifier of landmark from correspondence table
                z_t_i = z_t.elementAt(i);
                int j = z_t_i.signature;

                // LINE 11, simplicity calculation: actual position of landmark minus robot believed position
                double mjx_mutx = AGVsim.testbed.object_at(j).x - Mut_bar.get(0, 0);
                double mjy_muty = AGVsim.testbed.object_at(j).y - Mut_bar.get(1, 0);
                double q = Utils.square(mjx_mutx) + Utils.square(mjy_muty);
                out.println("xdist=" + mjx_mutx + " ydist=" + mjy_muty + " range=" + Math.sqrt(q));

                // LINE 12, z_hat is predicted observation based on robot's believed position
                double sqrt_q = Math.sqrt(q);
                Matrix z = new Matrix(3, 1);
                Matrix z_t_hat = new Matrix(3, 1);
                z.set(0, 0, z_t_i.actualRange);
                z.set(1, 0, z_t_i.actualAngle);
                z.set(2, 0, z_t_i.signature);
                z_t_hat.set(0, 0, sqrt_q);
                z_t_hat.set(1, 0, Math.atan2(mjy_muty, mjx_mutx) - Mut_bar.get(2, 0));
                z_t_hat.set(2, 0, z_t_i.signature);
                if (z_t_hat.get(1, 0) > Math.PI / 2) {
                    out.print("OLD = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
                    z_t_hat.set(1, 0, Utils.clamp_angle_within_pi_over_two(z_t_hat.get(1, 0)));
                    out.println("  NEW = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));

                }
                out.println("z    range=" + z.get(0, 0) + "\tangle=" + Math.toDegrees(z.get(1, 0)) + "\tsig=" + z.get(2, 0));
                out.println("zhat range=" + z_t_hat.get(0, 0) + "\tangle=" + Math.toDegrees(z_t_hat.get(1, 0)) + "\tsig=" + z_t_hat.get(2, 0));
                final double atan2 = Math.toDegrees(Math.atan2(mjy_muty, mjx_mutx));
                out.println("atan2=" + atan2 + " Mut_bar=" + Math.toDegrees(Mut_bar.get(2, 0)));

                out.println("z angle=" + Math.round(Math.toDegrees(z.get(1, 0)))
                        + " zhat angle=" + Math.round(Math.toDegrees(z_t_hat.get(1, 0)))
                        + " atan2=" + Math.round(atan2)
                        + " Mut_bar=" + Math.round(Math.toDegrees(Mut_bar.get(2, 0)))
                        + " mjx=" + mjx_mutx
                        + " mjy=" + mjy_muty);

                // LINE 13, H is Jacobian wrt to pose (x,y,theta) taken of h (measurement model)
                Matrix Ht = new Matrix(3, 3);
                Matrix HtT;
                Ht.set(0, 0, -mjx_mutx / sqrt_q);
                Ht.set(0, 1, -mjy_muty / sqrt_q);
                Ht.set(0, 2, 0);
                Ht.set(1, 0, mjy_muty / q);
                Ht.set(1, 1, -mjx_mutx / q);
                Ht.set(1, 2, -1);
                Ht.set(2, 0, 0);
                Ht.set(2, 1, 0);
                Ht.set(2, 2, 0);
                HtT = Ht.transpose();

                // LINE 14, S is the sum of two covariance matrices
                Matrix St = Ht.times(SIGMAt_bar.times(HtT)).plus(Qt);
                Matrix St_inv;
                if (St.det() == 0) {
                    St.print(10, 3);
                    out.println("Agent: S is a singular matrix, skipping correction " + i + ".");
                    continue;
                }
                St_inv = St.inverse();

                // LINE 15, Kalman gain
                Matrix Kt = SIGMAt_bar.times(HtT.times(St_inv));

                // LINE 16, innovation calculation - difference between observed & predicted position
                Mut_bar = Mut_bar.plus(Kt.times(z.minus(z_t_hat)));
                Mut_bar.set(2, 0, Utils.clamp_angle(Mut_bar.get(2, 0)));

                // LINE 17
                SIGMAt_bar = Matrix.identity(3, 3).minus(Kt.times(Ht)).times(SIGMAt_bar);
            } // LINE 18
        }

        // LINE 19
        pose = Mut_bar;
        sensorPose = pose;

        // LINE 20
        SIGMA = SIGMAt_bar;
    }

    void mcl() {
        AGVsim.testbed.move(control);
        sensor.sense(AGVsim.testbed);
        SensorReading z_t_i;
        Vector<SensorReading> z_t = sensor.get_hits();
        z_t_i = closest_hit(z_t);
        double[] weights = new double[numParticles];
        double Minv = 1.0 / (double) numParticles;

        Particle[] new_particles = new Particle[numParticles];
        for (int m = 0; m < numParticles; m++) {
            new_particles[m] = sample_motion_model_velocity(control, particles[m]);
            weights[m] = 0.0;
            if (z_t.size() >= 1)
                weights[m] = landmark_model_known_correspondence(
                        z_t_i, -1, new_particles[m], AGVsim.testbed);
            else weights[m] = Minv;
            out.println("Weight[" + m + "]=" + weights[m]);
        }
        if (this.enableCorrection)
            particles = low_variance_sampler(new_particles, weights);

        pose = new Matrix(3, 1);
        for (int m = 0; m < numParticles; m++) {
            pose.set(0, 0, pose.get(0, 0) + particles[m].x());
            pose.set(1, 0, pose.get(1, 0) + particles[m].y());
            pose.set(2, 0, pose.get(2, 0) + particles[m].theta());
        }
        pose.set(0, 0, pose.get(0, 0) / numParticles);
        pose.set(1, 0, pose.get(1, 0) / numParticles);
        pose.set(2, 0, Utils.clamp_angle(pose.get(2, 0) / numParticles));
    }

    SensorReading closest_hit(Vector<SensorReading> hits) {
        SensorReading sr = new SensorReading();
        double range = sensor.get_max_range();
        for (int i = 0; i < hits.size(); i++) {
            if (hits.elementAt(i).actualRange < range)
                sr = hits.elementAt(i);
        }
        return sr;
    }

    // Page 110
    // chi_t is the particle set
    // weights are the associated weights for each particle
    Particle[] low_variance_sampler(Particle[] chi_t, double[] weights) {
        Particle[] new_particles = new Particle[numParticles];
        double Minv = 1.0 / (double) numParticles;
        double r = Utils.rand(0.0, Minv);
        double c = weights[0];
        int i = 1;
        for (int m = 1; m <= numParticles; m++) {
            double U = r + (m - 1) * Minv;
            while (U > c && i < numParticles) {
                out.println("U=" + U + " c=" + c + " m=" + m + " r=" + r + " i=" + i);
                i++;
                c = c + weights[i - 1];
            }
            out.println("Adding particle " + i + " to the set.");
            new_particles[m - 1] = chi_t[i - 1];
        }
        return new_particles;
    }

    // Page 179
    // f is an observed feature
    // c is unused and represents correspondence variable - true identity of the feature
    // x_t is robot pose
    // m is map with objects
    double landmark_model_known_correspondence(SensorReading f, int c, Particle x_t, Testbed m) {
        int j = f.signature; // LINE 2 j = c_i_t
        SimObject o = m.object_at(j);
        out.println("BOT (X,Y)=(" + x_t.x() + ", " + x_t.y() + ") OBJ (X,Y)=(" + o.x() + ", " + o.y() + ") c=" + c);
        double r_hat = Math.sqrt(
                Utils.square(o.x() - x_t.x())
                        + Utils.square(o.y() - x_t.y())); // LINE 3
        double phi_hat = Math.atan2(o.y() - x_t.y(), o.x() - x_t.x()) - x_t.theta(); // LINE 4
        if (phi_hat > Math.PI / 2) {
            final long angle = Math.round(Math.toDegrees(phi_hat));
            out.print("OLD = " + angle);
            phi_hat = Utils.clamp_angle_within_pi_over_two(phi_hat);
            out.println("  old-angle=" + angle + " new-angle=" + phi_hat);
        }

        // LINE 5
        // f.actual_range represents r_i_t
        // Qt.get(0, 0) represents sigma_r, for example.
        out.println("p1=" + Utils.prob(f.actualRange - r_hat, Math.sqrt(Qt.get(0, 0)))
                + "r_hat=" + r_hat + " r_act=" + f.actualRange + "Q_sig_r=" + Math.sqrt(Qt.get(0, 0))
                + " p2=" + Utils.prob(f.actualAngle - phi_hat, Math.sqrt(Qt.get(1, 1)))
                + " p3=" + Utils.prob(0, Math.sqrt(Qt.get(2, 2))));
        return Utils.prob(f.actualRange - r_hat, Math.sqrt(Qt.get(0, 0))) *
                Utils.prob(f.actualAngle - phi_hat, Math.sqrt(Qt.get(1, 1))) *
                Utils.prob(0, Math.sqrt(Qt.get(2, 2))); // prob (s_i_t - s_j, sigma_s) ??
    }

    // Page 124 - give noise to a particle
    // u_t is a control action with v and omega (w)
    // x_t_minus_one is a particle which represents state of bot
    Particle sample_motion_model_velocity(Matrix u_t, Particle x_t_minus_one) {
        double v = u_t.get(0, 0);
        double w = u_t.get(1, 0);
        double v_abs = Math.abs(v);
        double w_abs = Math.abs(w);
        double v_hat = v + sample(a1Noise * v_abs + a2Noise * w_abs); // LINE 2
        double w_hat = w + sample(a3Noise * v_abs + a4Noise * w_abs); // LINE 3
        double gamma_hat = sample(a5Noise * v_abs + a6Noise * w_abs); // LINE 4
        double theta = x_t_minus_one.angle;
        double v_hat_over_w_hat = v_hat / w_hat;
        double theta_dtheta = theta + w_hat * Engine.deltaT;
        // LINES 5-7
        return new Particle(
                x_t_minus_one.x - v_hat_over_w_hat * Math.sin(theta) + v_hat_over_w_hat * Math.sin(theta_dtheta),
                x_t_minus_one.y + v_hat_over_w_hat * Math.cos(theta) - v_hat_over_w_hat * Math.cos(theta_dtheta),
                theta_dtheta + gamma_hat * Engine.deltaT);
    }

    // Give a randomly distributed sample from -val to val
    double sample(double val) {
        return sample_triangular_distribution(val);
    }

    // Page 124 - give a sample from a normal distribution. More costly than sampling triangular distribution
    double sample_normal_distribution(double b) {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += Utils.rand(-b, b);
        return 1.0 / 2.0 * sum;
    }

    // Page 124 - give a sample from a triangle distribution which is usually ok to approximate a sample from a normal distribution
    double sample_triangular_distribution(double b) {
        final double sqrt_six_over_two = Math.sqrt(6) / 2.0;
        return sqrt_six_over_two * (Utils.rand(-b, b) + Utils.rand(-b, b));
    }

    void act_and_observe() {
        if (AGVsim.algorithm == 1)
            ekf();
        if (AGVsim.algorithm == 2)
            mcl();
        AGVsim.logger.save_agent_pose(pose);
    }

    void set_sensor(Sensor sensor) {
        this.sensor = sensor;
    }

    double get_x_position() {
        return pose.get(0, 0);
    }

    double get_y_position() {
        return pose.get(1, 0);
    }

    double get_sensor_x_position() {
        return sensorPose.get(0, 0);
    }

    double get_sensor_y_position() {
        return sensorPose.get(1, 0);
    }

    double get_initial_x_position() {
        return subjectiveBotLocationX0;
    }

    double get_initial_y_position() {
        return subjectiveBotLocationY0;
    }

    double get_orientation() {
        return pose.get(2, 0);
    }

    void set_orientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        pose.set(2, 0, orient);
        sensorPose.set(2, 0, orient);
    }

    double get_sensor_orientation() {
        return sensorPose.get(2, 0);
    }

    void set_initial_orientation(double orient) {
        subjectiveBotOrientation0 = orient;
        sensorPose.set(2, 0, orient);
    }

    void set_initial_position(double x, double y) {
        subjectiveBotLocationX0 = x;
        subjectiveBotLocationY0 = y;
        sensorPose.set(0, 0, x);
        sensorPose.set(1, 0, y);
    }

    void set_position(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        sensorPose.set(0, 0, x);
        sensorPose.set(1, 0, y);
    }

    double get_translational_velocity() {
        return control.get(0, 0);
    }

    void set_translational_velocity(double csec) {
        if (csec > 0.0)
            control.set(0, 0, csec);
    }

    double get_rotational_velocity() {
        return control.get(1, 0);
    }

    void set_rotational_velocity(double rad_sec) {
        control.set(1, 0, rad_sec);
    }

    Matrix get_covar_mat(int index) {
        return switch (index) {
            case 0 -> rhsSIGMABar;
            case 1 -> lhsSIGMABar;
//            case 2 -> SIGMA;
            default -> SIGMA;
        };
    }


}
