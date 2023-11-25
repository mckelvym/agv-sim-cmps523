// Mark McKelvy
// CMPS 523
// Final Project
// File: Agent.java
package agv.sim.cmps523;

import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import Jama.Matrix;
import agv.sim.cmps523.data.Logger;
import agv.sim.cmps523.data.Particle;
import agv.sim.cmps523.data.SensorReading;
import agv.sim.cmps523.data.SimObject;
import agv.sim.cmps523.math.MathUtil;
import agv.sim.cmps523.type.SensorNoiseType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Vector;

// subjective model
public class Agent {

    private static Agent INSTANCE;
    private final Matrix control;
    private final Matrix Qt;
    private final Values values;
    private final Logger logger;
    private final List<Particle> particles;
    private Sensor sensor;
    private boolean configOrientation;
    private Matrix pose;
    private Matrix sensorPose;
    private Matrix SIGMA;
    private Matrix lhsSIGMABar;
    private Matrix rhsSIGMABar;
    private double subjectiveBotLocationX0; // initial subjective bot loc x
    private double subjectiveBotLocationY0; // initial subjective bot loc y
    private double subjectiveBotOrientation0;

    private Agent(Values values, Logger logger) {
        this.values = requireNonNull(values);
        this.logger = requireNonNull(logger);
        configOrientation = false;
        subjectiveBotLocationX0 = 180;
        subjectiveBotLocationY0 = 200;
        subjectiveBotOrientation0 = Math.PI / 2;
        pose = new Matrix(3, 1);
        sensorPose = new Matrix(3, 1);
        control = new Matrix(2, 1);
        SIGMA = new Matrix(3, 3);
        rhsSIGMABar = new Matrix(3, 3);
        lhsSIGMABar = new Matrix(3, 3);
        Qt = new Matrix(3, 3);
        particles = Lists.newArrayList();
        initializeSubjectiveBotPose();
        initializeAgentNoise();
        initializeParticles();
    }

    public static Agent newAgent(Values values, Logger logger) {
        INSTANCE = new Agent(values, logger);
        return getCurrent();
    }

    public static Agent getCurrent() {
        return requireNonNull(INSTANCE);
    }

    public void initializeParticles() {
        particles.clear();
        out.println("Initializing " + values.getNumberOfParticles() + " particles.");
        for (int i = 0; i < values.getNumberOfParticles(); i++) {
            particles.add(new Particle(
                    MathUtil.gaussian(subjectiveBotLocationX0, 1),
                    MathUtil.gaussian(subjectiveBotLocationY0, 1),
                    MathUtil.gaussian(subjectiveBotOrientation0, Math.toRadians(1))));
        }
    }

    private void initializeAgentNoise() {
        for (int i = 0; i <= 2; i++) {
            final SensorNoiseType sensorNoiseType = SensorNoiseType.at(i);
            final double sensorNoise = values.getSensorNoise(sensorNoiseType);
            Qt.set(i, i, sensorNoise);
            String index;
            if (i == 0)
                index = "range";
            else if (i == 1)
                index = "bearing";
            else
                index = "signature";
            out.println("Agent: Qt noise sigma[" + index + "]^2 = " + Qt.get(i, i));
        }
        switch (values.getAlgorithmType()) {
            case EXTENDED_KALMAN_FILTER:
                out.println("Agent: alpha noise: " + values.getAlphaNoise(1) + " " + values.getAlphaNoise(2) + " " + values.getAlphaNoise(3) + " " + values.getAlphaNoise(4));
                break;
            case MONTE_CARLO_LOCALIZATION: {
                out.println("Agent: alpha noise: " + values.getAlphaNoise(1) + " " + values.getAlphaNoise(2) + " " + values.getAlphaNoise(3) + " " + values.getAlphaNoise(4) + " " + values.getAlphaNoise(5) + " " + values.getAlphaNoise(6));
            }
            break;
        }

    }

    public void initializeSubjectiveBotPose() {
        setPosition(subjectiveBotLocationX0, subjectiveBotLocationY0);
        setOrientation(subjectiveBotOrientation0);
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                SIGMA.set(x, y, 0.0);
            }
        }

        logger.addAgentPose(pose);
    }

    private void syncNoise() {
        for (int i = 0; i <= 2; i++) {
            final SensorNoiseType sensorNoiseType = SensorNoiseType.at(i);
            final double sensorNoise = values.getSensorNoise(sensorNoiseType);
            Qt.set(i, i, sensorNoise);
        }
    }

    private void ekf() {
        final Matrix control = getControl();
        Testbed.getCurrent().move(control);

        double dt = values.getTimestampDelta();        // delta time
        double v = values.getAgentTranslationalVelocity();            // v from robot motion - translational velocity
        double w = values.getAgentRotationalVelocity();            // omega from robot motion - rotational velocity
        double vOverW = (w != 0.0) ? v / w : 0;
        double wSqr = w * w;
        double theta = getOrientation(); // LINE 2
        double dtheta = w * dt;
        double thetaDtheta = theta + dtheta;
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double cosThetaDtheta = Math.cos(thetaDtheta);
        double sinThetaDtheta = Math.sin(thetaDtheta);

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
        Gt.set(0, 2, vOverW * (-cosTheta + cosThetaDtheta));
        Gt.set(1, 2, vOverW * (-sinTheta + sinThetaDtheta));
        GtT = Gt.transpose();

        // LINE 4
        // Jacobian for linearized motion model wrt to control (v,w) taken of g (motion model)
        // taken at u_t and mu_{t-1}
        Vt = new Matrix(3, 2);
        // column 1
        Vt.set(0, 0, -(sinTheta + sinThetaDtheta) / w);
        Vt.set(1, 0, (cosTheta - cosThetaDtheta) / w);
        Vt.set(2, 0, 0);
        // column 2
        Vt.set(0, 1,
                (v * (sinTheta - sinThetaDtheta)) / wSqr +
                        (v * cosThetaDtheta * dt) / w
        );
        Vt.set(1, 1,
                -(v * (cosTheta - cosThetaDtheta)) / wSqr +
                        (v * sinThetaDtheta * dt) / w
        );
        Vt.set(2, 1, dt);
        VtT = Vt.transpose();

        // LINE 5, motion noise covariance matrix in control space
        Mt = Matrix.identity(2, 2);
        out.println("Agent: alpha noise: " + values.getAlphaNoise(1) + " " + values.getAlphaNoise(2) + " " + values.getAlphaNoise(3) + " " + values.getAlphaNoise(4));
        Mt.set(0, 0, MathUtil.square(values.getAlphaNoise(1) * v + values.getAlphaNoise(2) * w));
        Mt.set(1, 1, MathUtil.square(values.getAlphaNoise(3) * v + values.getAlphaNoise(4) * w));

        // LINE 6, motion update from motion model
        Mut_update = new Matrix(3, 1);
        Mut_update.set(0, 0, -(vOverW * sinTheta) + (vOverW * sinThetaDtheta));
        Mut_update.set(1, 0, (vOverW * cosTheta) - (vOverW * cosThetaDtheta));
        Mut_update.set(2, 0, dtheta);

        Mut_bar = pose;
        Mut_bar = Mut_bar.plus(Mut_update);
        Mut_bar.set(2, 0, MathUtil.clampAngle(Mut_bar.get(2, 0)));
        out.println("Believed Pose: " + Mut_bar.get(0, 0) + " " + Mut_bar.get(1, 0) + " " + Math.toDegrees(Mut_bar.get(2, 0)));

        // LINE 7, motion covariance matrix update from motion model
        // lhs of plus: predicted belief based on Jacobian control actions and SIGMA
        // rhs of plus: mapping between motion noise in control space to motion noise in state space.
        lhsSIGMABar = Gt.times(SIGMA.times(GtT));
        rhsSIGMABar = Vt.times(Mt.times(VtT));

        SIGMAt_bar = lhsSIGMABar.plus(rhsSIGMABar);

        sensorPose = Mut_bar;
        if (sensor != null)
            sensor.sense(Testbed.getCurrent());

        // LINE 8, covariance matrix for error in laser range finder
        // setup in function initialize_agent_noise();

        if (sensor != null && values.isEnableCorrection()) {
            out.println("Correction Step");

            // LINE 9, for each actual observation...
            SensorReading z_t_i;
            Vector<SensorReading> z_t = sensor.getHits();
            for (int i = 0; i < z_t.size(); i++) {
                out.println("Processing hit..");

                // LINE 10, unique identifier of landmark from correspondence table
                z_t_i = z_t.elementAt(i);
                int j = z_t_i.signature();

                // LINE 11, simplicity calculation: actual position of landmark minus robot believed position
                final SimObject simObject = Iterables.get(values.getSimObjects(), j);
                double mjx_mutx = simObject.x() - Mut_bar.get(0, 0);
                double mjy_muty = simObject.y() - Mut_bar.get(1, 0);
                double q = MathUtil.square(mjx_mutx) + MathUtil.square(mjy_muty);
                out.println("xdist=" + mjx_mutx + " ydist=" + mjy_muty + " range=" + Math.sqrt(q));

                // LINE 12, z_hat is predicted observation based on robot's believed position
                double sqrt_q = Math.sqrt(q);
                Matrix z = new Matrix(3, 1);
                Matrix z_t_hat = new Matrix(3, 1);
                z.set(0, 0, z_t_i.actualRange());
                z.set(1, 0, z_t_i.actualAngle());
                z.set(2, 0, z_t_i.signature());
                z_t_hat.set(0, 0, sqrt_q);
                z_t_hat.set(1, 0, Math.atan2(mjy_muty, mjx_mutx) - Mut_bar.get(2, 0));
                z_t_hat.set(2, 0, z_t_i.signature());
                if (z_t_hat.get(1, 0) > Math.PI / 2) {
                    out.print("OLD = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
                    z_t_hat.set(1, 0, MathUtil.clampAngleWithinPiOverTwo(z_t_hat.get(1, 0)));
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
                syncNoise();
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
                Mut_bar.set(2, 0, MathUtil.clampAngle(Mut_bar.get(2, 0)));

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

    private void mcl() {
        final Matrix control = getControl();
        Testbed.getCurrent().move(control);
        sensor.sense(Testbed.getCurrent());
        SensorReading z_t_i;
        Vector<SensorReading> z_t = sensor.getHits();
        z_t_i = closestHit(z_t);
        double[] weights = new double[values.getNumberOfParticles()];
        double Minv = 1.0 / (double) values.getNumberOfParticles();

        Particle[] newParticles = new Particle[values.getNumberOfParticles()];
        for (int m = 0; m < values.getNumberOfParticles(); m++) {
            newParticles[m] = sampleMotionModelVelocity(control, particles.get(m));
            weights[m] = 0.0;
            if (z_t.size() >= 1)
                weights[m] = landmarkModelKnownCorrespondence(
                        z_t_i, -1, newParticles[m], Testbed.getCurrent());
            else weights[m] = Minv;
            out.println("Weight[" + m + "]=" + weights[m]);
        }
        if (values.isEnableCorrection()) {
            Particle[] p = lowVarianceSampler(newParticles, weights);
            particles.clear();
            particles.addAll(asList(p));
        }

        pose = new Matrix(3, 1);
        for (Particle p : getParticles()) {
            pose.set(0, 0, pose.get(0, 0) + p.x());
            pose.set(1, 0, pose.get(1, 0) + p.y());
            pose.set(2, 0, pose.get(2, 0) + p.angle());
        }
        pose.set(0, 0, pose.get(0, 0) / values.getNumberOfParticles());
        pose.set(1, 0, pose.get(1, 0) / values.getNumberOfParticles());
        pose.set(2, 0, MathUtil.clampAngle(pose.get(2, 0) / values.getNumberOfParticles()));
    }

    private SensorReading closestHit(Vector<SensorReading> hits) {
        SensorReading sr = new SensorReading(0, -1, 0, 0, -1, 0, 0, 0, 0);
        double range = sensor.getMaxRange();
        for (int i = 0; i < hits.size(); i++) {
            if (hits.elementAt(i).actualRange() < range)
                sr = hits.elementAt(i);
        }
        return sr;
    }

    // Page 110
    // chi_t is the particle set
    // weights are the associated weights for each particle
    private Particle[] lowVarianceSampler(Particle[] chi_t, double[] weights) {
        Particle[] newParticles = new Particle[values.getNumberOfParticles()];
        double Minv = 1.0 / (double) values.getNumberOfParticles();
        double r = MathUtil.rand(0.0, Minv);
        double c = weights[0];
        int i = 1;
        for (int m = 1; m <= values.getNumberOfParticles(); m++) {
            double U = r + (m - 1) * Minv;
            while (U > c && i < values.getNumberOfParticles()) {
                out.println("U=" + U + " c=" + c + " m=" + m + " r=" + r + " i=" + i);
                i++;
                c = c + weights[i - 1];
            }
            out.println("Adding particle " + i + " to the set.");
            newParticles[m - 1] = chi_t[i - 1];
        }
        return newParticles;
    }

    // Page 179
    // f is an observed feature
    // c is unused and represents correspondence variable - true identity of the feature
    // x_t is robot pose
    // m is map with objects
    private double landmarkModelKnownCorrespondence(SensorReading f, int c, Particle x_t, Testbed m) {
        int j = f.signature(); // LINE 2 j = c_i_t
        SimObject o = Iterables.get(values.getSimObjects(), j);
        out.println("BOT (X,Y)=(" + x_t.x() + ", " + x_t.y() + ") OBJ (X,Y)=(" + o.x() + ", " + o.y() + ") c=" + c);
        double r_hat = Math.sqrt(
                MathUtil.square(o.x() - x_t.x())
                        + MathUtil.square(o.y() - x_t.y())); // LINE 3
        double phi_hat = Math.atan2(o.y() - x_t.y(), o.x() - x_t.x()) - x_t.angle(); // LINE 4
        if (phi_hat > Math.PI / 2) {
            final long angle = Math.round(Math.toDegrees(phi_hat));
            out.print("OLD = " + angle);
            phi_hat = MathUtil.clampAngleWithinPiOverTwo(phi_hat);
            out.println("  old-angle=" + angle + " new-angle=" + phi_hat);
        }

        // LINE 5
        // f.actual_range represents r_i_t
        // Qt.get(0, 0) represents sigma_r, for example.
        syncNoise();
        out.println("p1=" + MathUtil.prob(f.actualRange() - r_hat, Math.sqrt(Qt.get(0, 0)))
                + "r_hat=" + r_hat + " r_act=" + f.actualRange() + "Q_sig_r=" + Math.sqrt(Qt.get(0, 0))
                + " p2=" + MathUtil.prob(f.actualAngle() - phi_hat, Math.sqrt(Qt.get(1, 1)))
                + " p3=" + MathUtil.prob(0, Math.sqrt(Qt.get(2, 2))));
        return MathUtil.prob(f.actualRange() - r_hat, Math.sqrt(Qt.get(0, 0))) *
                MathUtil.prob(f.actualAngle() - phi_hat, Math.sqrt(Qt.get(1, 1))) *
                MathUtil.prob(0, Math.sqrt(Qt.get(2, 2))); // prob (s_i_t - s_j, sigma_s) ??
    }

    // Page 124 - give noise to a particle
    // u_t is a control action with v and omega (w)
    // x_t_minus_one is a particle which represents state of bot
    private Particle sampleMotionModelVelocity(Matrix u_t, Particle x_t_minus_one) {
        double v = u_t.get(0, 0);
        double w = u_t.get(1, 0);
        double v_abs = Math.abs(v);
        double w_abs = Math.abs(w);
        double v_hat = v + sample(values.getAlphaNoise(1) * v_abs + values.getAlphaNoise(2) * w_abs); // LINE 2
        double w_hat = w + sample(values.getAlphaNoise(3) * v_abs + values.getAlphaNoise(4) * w_abs); // LINE 3
        double gamma_hat = sample(values.getAlphaNoise(5) * v_abs + values.getAlphaNoise(6) * w_abs); // LINE 4
        double theta = x_t_minus_one.angle();
        double v_hat_over_w_hat = v_hat / w_hat;
        double theta_dtheta = theta + w_hat * values.getTimestampDelta();
        // LINES 5-7
        return new Particle(
                x_t_minus_one.x() - v_hat_over_w_hat * Math.sin(theta) + v_hat_over_w_hat * Math.sin(theta_dtheta),
                x_t_minus_one.y() + v_hat_over_w_hat * Math.cos(theta) - v_hat_over_w_hat * Math.cos(theta_dtheta),
                theta_dtheta + gamma_hat * values.getTimestampDelta());
    }

    // Give a randomly distributed sample from -val to val
    private double sample(double val) {
        return sampleTriangularDistribution(val);
    }

    // Page 124 - give a sample from a normal distribution. More costly than sampling triangular distribution
    private double sampleNormalDistribution(double b) {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += MathUtil.rand(-b, b);
        return 1.0 / 2.0 * sum;
    }

    // Page 124 - give a sample from a triangle distribution which is usually ok to approximate a sample from a normal distribution
    private double sampleTriangularDistribution(double b) {
        final double sqrt_six_over_two = Math.sqrt(6) / 2.0;
        return sqrt_six_over_two * (MathUtil.rand(-b, b) + MathUtil.rand(-b, b));
    }

    public void actAndObserve() {
        switch (values.getAlgorithmType()) {
            case EXTENDED_KALMAN_FILTER -> ekf();
            case MONTE_CARLO_LOCALIZATION -> mcl();
        }
        logger.addAgentPose(pose);
    }

    public double getXPosition() {
        return pose.get(0, 0);
    }

    public double getYPosition() {
        return pose.get(1, 0);
    }

    public double getSensorXPosition() {
        return sensorPose.get(0, 0);
    }

    public double getSensorYPosition() {
        return sensorPose.get(1, 0);
    }

    public double getInitialXPosition() {
        return subjectiveBotLocationX0;
    }

    public double getInitialYPosition() {
        return subjectiveBotLocationY0;
    }

    public double getOrientation() {
        return pose.get(2, 0);
    }

    private void setOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        pose.set(2, 0, orient);
        sensorPose.set(2, 0, orient);
    }

    public double getSensorOrientation() {
        return sensorPose.get(2, 0);
    }

    public void setInitialOrientation(double orient) {
        subjectiveBotOrientation0 = orient;
        sensorPose.set(2, 0, orient);
    }

    public void setInitialPosition(double x, double y) {
        subjectiveBotLocationX0 = x;
        subjectiveBotLocationY0 = y;
        sensorPose.set(0, 0, x);
        sensorPose.set(1, 0, y);
    }

    private void setPosition(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        sensorPose.set(0, 0, x);
        sensorPose.set(1, 0, y);
    }

    private Matrix getControl() {
        control.set(0, 0, values.getAgentTranslationalVelocity());
        control.set(1, 0, values.getAgentRotationalVelocity());
        return control;
    }

    public Matrix getCovarMat(int index) {
        return switch (index) {
            case 0 -> rhsSIGMABar;
            case 1 -> lhsSIGMABar;
//            case 2 -> SIGMA;
            default -> SIGMA;
        };
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public boolean isConfigOrientation() {
        return configOrientation;
    }

    public void setConfigOrientation(boolean configOrientation) {
        this.configOrientation = configOrientation;
    }
}
