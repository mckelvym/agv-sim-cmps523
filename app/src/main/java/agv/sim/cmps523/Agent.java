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
    private Sensor sensor;
    private boolean enableCorrection;
    private int numParticles = 100;
    private Particle[] particles;
    // agent noise
    private double a1Noise = 0.0; // default value
    private double a2Noise = 0.0; // default value
    private double a3Noise = 0.0; // default value
    private double a4Noise = 0.0; // default value
    private double a5Noise = 0.0; // default value
    private double a6Noise = 0.0; // default value
    private Matrix Qt;
    private boolean configOrientation = false;
    private Matrix pose;
    private Matrix sensorPose;
    private Matrix SIGMA;
    private Matrix lhsSIGMABar;
    private Matrix rhsSIGMABar;
    private double subjectiveBotLocationX0 = 180; // initial subjective bot loc x
    private double subjectiveBotLocationY0 = 200; // initial subjective bot loc y
    private double subjectiveBotOrientation0 = Math.PI / 2;

    public Agent() {
        setPose(new Matrix(3, 1));
        setSensorPose(new Matrix(3, 1));
        control = new Matrix(2, 1);
        setSIGMA(new Matrix(3, 3));
        setRhsSIGMABar(getSIGMA());
        setLhsSIGMABar(getSIGMA());
        setQt(new Matrix(3, 3));
        initializeSubjectiveBotPose();
        initializeAgentNoise();
        initializeParticles();
        setEnableCorrection(true);
    }

    void initializeParticles() {
        setNumParticles(ParticleDialog.getNumberParticles());
        setParticles(new Particle[getNumParticles()]);
        out.println("Initting " + getNumParticles() + " particles.");
        for (int i = 0; i < getNumParticles(); i++) {
            getParticles()[i] = new Particle(
                    Utils.gaussian(getSubjectiveBotLocationX0(), 1),
                    Utils.gaussian(getSubjectiveBotLocationY0(), 1),
                    Utils.gaussian(getSubjectiveBotOrientation0(), Math.toRadians(1))
            );
        }
    }

    void initializeAgentNoise() {
        setA1Noise(NoiseControlPanel.getAlphaNoise(1));
        setA2Noise(NoiseControlPanel.getAlphaNoise(2));
        setA3Noise(NoiseControlPanel.getAlphaNoise(3));
        setA4Noise(NoiseControlPanel.getAlphaNoise(4));
        for (int i = 0; i <= 2; i++) {
            getQt().set(i, i, NoiseControlPanel.getSensorNoise(i + 1));
            String index;
            if (i == 0)
                index = "range";
            else if (i == 1)
                index = "bearing";
            else
                index = "signature";
            out.println("Agent: Qt noise sigma[" + index + "]^2 = " + getQt().get(i, i));
        }
        if (AGVsim.getAlgorithm() == 1) {
            out.println("Agent: alpha noise: " + getA1Noise() + " " + getA2Noise() + " " + getA3Noise() + " " + getA4Noise());
        } else {
            setA5Noise(NoiseControlPanel.getAlphaNoise(5));
            setA6Noise(NoiseControlPanel.getAlphaNoise(6));
            out.println("Agent: alpha noise: " + getA1Noise() + " " + getA2Noise() + " " + getA3Noise() + " " + getA4Noise() + " " + getA5Noise() + " " + getA6Noise());
        }
    }

    void initializeSubjectiveBotPose() {
        setPosition(getSubjectiveBotLocationX0(), getSubjectiveBotLocationY0());
        setOrientation(getSubjectiveBotOrientation0());
        getControl().set(0, 0, 0);
        getControl().set(1, 0, 0);
        getSIGMA().set(0, 0, 0.0);
        getSIGMA().set(0, 1, 0.0);
        getSIGMA().set(0, 2, 0.0);
        getSIGMA().set(1, 0, 0.0);
        getSIGMA().set(1, 1, 0.0);
        getSIGMA().set(1, 2, 0.0);
        getSIGMA().set(2, 0, 0.0);
        getSIGMA().set(2, 1, 0.0);
        getSIGMA().set(2, 2, 0.0);

        AGVsim.getLogger().saveAgentPose(getPose());
    }

    void ekf() {
        AGVsim.getTestbed().move(getControl());

        double dt = Engine.getDeltaT();        // delta time
        double v = (getControl().get(0, 0));            // v from robot motion - translational velocity
        double w = (getControl().get(1, 0));            // omega from robot motion - rotataional velocity
        double v_over_w = (w != 0.0) ? v / w : 0;
        double w_sqr = w * w;
        double theta = getOrientation(); // LINE 2
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
        out.println("Agent: alpha noise: " + getA1Noise() + " " + getA2Noise() + " " + getA3Noise() + " " + getA4Noise());
        Mt.set(0, 0, Utils.square(getA1Noise() * v + getA2Noise() * w));
        Mt.set(1, 1, Utils.square(getA3Noise() * v + getA4Noise() * w));

        // LINE 6, motion update from motion model
        Mut_update = new Matrix(3, 1);
        Mut_update.set(0, 0, -(v_over_w * sin_theta) + (v_over_w * sin_theta_dtheta));
        Mut_update.set(1, 0, (v_over_w * cos_theta) - (v_over_w * cos_theta_dtheta));
        Mut_update.set(2, 0, dtheta);

        Mut_bar = getPose();
        Mut_bar = Mut_bar.plus(Mut_update);
        Mut_bar.set(2, 0, Utils.clampAngle(Mut_bar.get(2, 0)));
        out.println("Believed Pose: " + Mut_bar.get(0, 0) + " " + Mut_bar.get(1, 0) + " " + Math.toDegrees(Mut_bar.get(2, 0)));

        // LINE 7, motion covariance matrix update from motion model
        // lhs of plus: predicted belief based on Jacobian control actions and SIGMA
        // rhs of plus: mapping between motion noise in control space to motion noise in state space.
        setLhsSIGMABar(Gt.times(getSIGMA().times(GtT)));
        setRhsSIGMABar(Vt.times(Mt.times(VtT)));

        SIGMAt_bar = getLhsSIGMABar().plus(getRhsSIGMABar());

        setSensorPose(Mut_bar);
        if (getSensor() != null)
            getSensor().sense(AGVsim.getTestbed());

        // LINE 8, covariance matrix for error in laser range finder
        // setup in function initialize_agent_noise();

        if (getSensor() != null && isEnableCorrection()) {
            out.println("Correction Step");

            // LINE 9, for each actual observation...
            SensorReading z_t_i;
            Vector<SensorReading> z_t = getSensor().get_hits();
            for (int i = 0; i < z_t.size(); i++) {
                out.println("Processing hit..");

                // LINE 10, unique identifier of landmark from correspondence table
                z_t_i = z_t.elementAt(i);
                int j = z_t_i.getSignature();

                // LINE 11, simplicity calculation: actual position of landmark minus robot believed position
                double mjx_mutx = AGVsim.getTestbed().objectAt(j).getX() - Mut_bar.get(0, 0);
                double mjy_muty = AGVsim.getTestbed().objectAt(j).getY() - Mut_bar.get(1, 0);
                double q = Utils.square(mjx_mutx) + Utils.square(mjy_muty);
                out.println("xdist=" + mjx_mutx + " ydist=" + mjy_muty + " range=" + Math.sqrt(q));

                // LINE 12, z_hat is predicted observation based on robot's believed position
                double sqrt_q = Math.sqrt(q);
                Matrix z = new Matrix(3, 1);
                Matrix z_t_hat = new Matrix(3, 1);
                z.set(0, 0, z_t_i.getActualRange());
                z.set(1, 0, z_t_i.getActualAngle());
                z.set(2, 0, z_t_i.getSignature());
                z_t_hat.set(0, 0, sqrt_q);
                z_t_hat.set(1, 0, Math.atan2(mjy_muty, mjx_mutx) - Mut_bar.get(2, 0));
                z_t_hat.set(2, 0, z_t_i.getSignature());
                if (z_t_hat.get(1, 0) > Math.PI / 2) {
                    out.print("OLD = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
                    z_t_hat.set(1, 0, Utils.clampAngleWithinPiOverTwo(z_t_hat.get(1, 0)));
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
                Matrix St = Ht.times(SIGMAt_bar.times(HtT)).plus(getQt());
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
                Mut_bar.set(2, 0, Utils.clampAngle(Mut_bar.get(2, 0)));

                // LINE 17
                SIGMAt_bar = Matrix.identity(3, 3).minus(Kt.times(Ht)).times(SIGMAt_bar);
            } // LINE 18
        }

        // LINE 19
        setPose(Mut_bar);
        setSensorPose(getPose());

        // LINE 20
        setSIGMA(SIGMAt_bar);
    }

    void mcl() {
        AGVsim.getTestbed().move(getControl());
        getSensor().sense(AGVsim.getTestbed());
        SensorReading z_t_i;
        Vector<SensorReading> z_t = getSensor().get_hits();
        z_t_i = closestHit(z_t);
        double[] weights = new double[getNumParticles()];
        double Minv = 1.0 / (double) getNumParticles();

        Particle[] new_particles = new Particle[getNumParticles()];
        for (int m = 0; m < getNumParticles(); m++) {
            new_particles[m] = sampleMotionModelVelocity(getControl(), getParticles()[m]);
            weights[m] = 0.0;
            if (z_t.size() >= 1)
                weights[m] = landmarkModelKnownCorrespondence(
                        z_t_i, -1, new_particles[m], AGVsim.getTestbed());
            else weights[m] = Minv;
            out.println("Weight[" + m + "]=" + weights[m]);
        }
        if (this.isEnableCorrection())
            setParticles(lowVarianceSampler(new_particles, weights));

        setPose(new Matrix(3, 1));
        for (int m = 0; m < getNumParticles(); m++) {
            getPose().set(0, 0, getPose().get(0, 0) + getParticles()[m].x());
            getPose().set(1, 0, getPose().get(1, 0) + getParticles()[m].y());
            getPose().set(2, 0, getPose().get(2, 0) + getParticles()[m].theta());
        }
        getPose().set(0, 0, getPose().get(0, 0) / getNumParticles());
        getPose().set(1, 0, getPose().get(1, 0) / getNumParticles());
        getPose().set(2, 0, Utils.clampAngle(getPose().get(2, 0) / getNumParticles()));
    }

    SensorReading closestHit(Vector<SensorReading> hits) {
        SensorReading sr = new SensorReading();
        double range = getSensor().get_max_range();
        for (int i = 0; i < hits.size(); i++) {
            if (hits.elementAt(i).getActualRange() < range)
                sr = hits.elementAt(i);
        }
        return sr;
    }

    // Page 110
    // chi_t is the particle set
    // weights are the associated weights for each particle
    Particle[] lowVarianceSampler(Particle[] chi_t, double[] weights) {
        Particle[] new_particles = new Particle[getNumParticles()];
        double Minv = 1.0 / (double) getNumParticles();
        double r = Utils.rand(0.0, Minv);
        double c = weights[0];
        int i = 1;
        for (int m = 1; m <= getNumParticles(); m++) {
            double U = r + (m - 1) * Minv;
            while (U > c && i < getNumParticles()) {
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
    double landmarkModelKnownCorrespondence(SensorReading f, int c, Particle x_t, Testbed m) {
        int j = f.getSignature(); // LINE 2 j = c_i_t
        SimObject o = m.objectAt(j);
        out.println("BOT (X,Y)=(" + x_t.x() + ", " + x_t.y() + ") OBJ (X,Y)=(" + o.x() + ", " + o.y() + ") c=" + c);
        double r_hat = Math.sqrt(
                Utils.square(o.x() - x_t.x())
                        + Utils.square(o.y() - x_t.y())); // LINE 3
        double phi_hat = Math.atan2(o.y() - x_t.y(), o.x() - x_t.x()) - x_t.theta(); // LINE 4
        if (phi_hat > Math.PI / 2) {
            final long angle = Math.round(Math.toDegrees(phi_hat));
            out.print("OLD = " + angle);
            phi_hat = Utils.clampAngleWithinPiOverTwo(phi_hat);
            out.println("  old-angle=" + angle + " new-angle=" + phi_hat);
        }

        // LINE 5
        // f.actual_range represents r_i_t
        // Qt.get(0, 0) represents sigma_r, for example.
        out.println("p1=" + Utils.prob(f.getActualRange() - r_hat, Math.sqrt(getQt().get(0, 0)))
                + "r_hat=" + r_hat + " r_act=" + f.getActualRange() + "Q_sig_r=" + Math.sqrt(getQt().get(0, 0))
                + " p2=" + Utils.prob(f.getActualAngle() - phi_hat, Math.sqrt(getQt().get(1, 1)))
                + " p3=" + Utils.prob(0, Math.sqrt(getQt().get(2, 2))));
        return Utils.prob(f.getActualRange() - r_hat, Math.sqrt(getQt().get(0, 0))) *
                Utils.prob(f.getActualAngle() - phi_hat, Math.sqrt(getQt().get(1, 1))) *
                Utils.prob(0, Math.sqrt(getQt().get(2, 2))); // prob (s_i_t - s_j, sigma_s) ??
    }

    // Page 124 - give noise to a particle
    // u_t is a control action with v and omega (w)
    // x_t_minus_one is a particle which represents state of bot
    Particle sampleMotionModelVelocity(Matrix u_t, Particle x_t_minus_one) {
        double v = u_t.get(0, 0);
        double w = u_t.get(1, 0);
        double v_abs = Math.abs(v);
        double w_abs = Math.abs(w);
        double v_hat = v + sample(getA1Noise() * v_abs + getA2Noise() * w_abs); // LINE 2
        double w_hat = w + sample(getA3Noise() * v_abs + getA4Noise() * w_abs); // LINE 3
        double gamma_hat = sample(getA5Noise() * v_abs + getA6Noise() * w_abs); // LINE 4
        double theta = x_t_minus_one.getAngle();
        double v_hat_over_w_hat = v_hat / w_hat;
        double theta_dtheta = theta + w_hat * Engine.getDeltaT();
        // LINES 5-7
        return new Particle(
                x_t_minus_one.getX() - v_hat_over_w_hat * Math.sin(theta) + v_hat_over_w_hat * Math.sin(theta_dtheta),
                x_t_minus_one.getY() + v_hat_over_w_hat * Math.cos(theta) - v_hat_over_w_hat * Math.cos(theta_dtheta),
                theta_dtheta + gamma_hat * Engine.getDeltaT());
    }

    // Give a randomly distributed sample from -val to val
    double sample(double val) {
        return sampleTriangularDistribution(val);
    }

    // Page 124 - give a sample from a normal distribution. More costly than sampling triangular distribution
    double sampleNormalDistribution(double b) {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += Utils.rand(-b, b);
        return 1.0 / 2.0 * sum;
    }

    // Page 124 - give a sample from a triangle distribution which is usually ok to approximate a sample from a normal distribution
    double sampleTriangularDistribution(double b) {
        final double sqrt_six_over_two = Math.sqrt(6) / 2.0;
        return sqrt_six_over_two * (Utils.rand(-b, b) + Utils.rand(-b, b));
    }

    void actAndObserve() {
        if (AGVsim.getAlgorithm() == 1)
            ekf();
        if (AGVsim.getAlgorithm() == 2)
            mcl();
        AGVsim.getLogger().saveAgentPose(getPose());
    }

    double getXPosition() {
        return getPose().get(0, 0);
    }

    double getYPosition() {
        return getPose().get(1, 0);
    }

    double getSensorXPosition() {
        return getSensorPose().get(0, 0);
    }

    double getSensorYPosition() {
        return getSensorPose().get(1, 0);
    }

    double getInitialXPosition() {
        return getSubjectiveBotLocationX0();
    }

    double getInitialYPosition() {
        return getSubjectiveBotLocationY0();
    }

    double getOrientation() {
        return getPose().get(2, 0);
    }

    void setOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        getPose().set(2, 0, orient);
        getSensorPose().set(2, 0, orient);
    }

    double getSensorOrientation() {
        return getSensorPose().get(2, 0);
    }

    void setInitialOrientation(double orient) {
        setSubjectiveBotOrientation0(orient);
        getSensorPose().set(2, 0, orient);
    }

    void setInitialPosition(double x, double y) {
        setSubjectiveBotLocationX0(x);
        setSubjectiveBotLocationY0(y);
        getSensorPose().set(0, 0, x);
        getSensorPose().set(1, 0, y);
    }

    void setPosition(double x, double y) {
        getPose().set(0, 0, x);
        getPose().set(1, 0, y);
        getSensorPose().set(0, 0, x);
        getSensorPose().set(1, 0, y);
    }

    double getTranslationalVelocity() {
        return getControl().get(0, 0);
    }

    void setTranslationalVelocity(double csec) {
        if (csec > 0.0)
            getControl().set(0, 0, csec);
    }

    double getRotationalVelocity() {
        return getControl().get(1, 0);
    }

    void setRotationalVelocity(double rad_sec) {
        getControl().set(1, 0, rad_sec);
    }

    Matrix getCovarMat(int index) {
        return switch (index) {
            case 0 -> getRhsSIGMABar();
            case 1 -> getLhsSIGMABar();
//            case 2 -> SIGMA;
            default -> getSIGMA();
        };
    }

    public Matrix getControl() {
        return control;
    }

    public Sensor getSensor() {
        return sensor;
    }

    void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public boolean isEnableCorrection() {
        return enableCorrection;
    }

    public void setEnableCorrection(boolean enableCorrection) {
        this.enableCorrection = enableCorrection;
    }

    public int getNumParticles() {
        return numParticles;
    }

    public void setNumParticles(int numParticles) {
        this.numParticles = numParticles;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public void setParticles(Particle[] particles) {
        this.particles = particles;
    }

    public double getA1Noise() {
        return a1Noise;
    }

    public void setA1Noise(double a1Noise) {
        this.a1Noise = a1Noise;
    }

    public double getA2Noise() {
        return a2Noise;
    }

    public void setA2Noise(double a2Noise) {
        this.a2Noise = a2Noise;
    }

    public double getA3Noise() {
        return a3Noise;
    }

    public void setA3Noise(double a3Noise) {
        this.a3Noise = a3Noise;
    }

    public double getA4Noise() {
        return a4Noise;
    }

    public void setA4Noise(double a4Noise) {
        this.a4Noise = a4Noise;
    }

    public double getA5Noise() {
        return a5Noise;
    }

    public void setA5Noise(double a5Noise) {
        this.a5Noise = a5Noise;
    }

    public double getA6Noise() {
        return a6Noise;
    }

    public void setA6Noise(double a6Noise) {
        this.a6Noise = a6Noise;
    }

    public Matrix getQt() {
        return Qt;
    }

    public void setQt(Matrix qt) {
        Qt = qt;
    }

    public boolean isConfigOrientation() {
        return configOrientation;
    }

    public void setConfigOrientation(boolean configOrientation) {
        this.configOrientation = configOrientation;
    }

    public Matrix getPose() {
        return pose;
    }

    public void setPose(Matrix pose) {
        this.pose = pose;
    }

    public Matrix getSensorPose() {
        return sensorPose;
    }

    public void setSensorPose(Matrix sensorPose) {
        this.sensorPose = sensorPose;
    }

    public Matrix getSIGMA() {
        return SIGMA;
    }

    public void setSIGMA(Matrix SIGMA) {
        this.SIGMA = SIGMA;
    }

    public Matrix getLhsSIGMABar() {
        return lhsSIGMABar;
    }

    public void setLhsSIGMABar(Matrix lhsSIGMABar) {
        this.lhsSIGMABar = lhsSIGMABar;
    }

    public Matrix getRhsSIGMABar() {
        return rhsSIGMABar;
    }

    public void setRhsSIGMABar(Matrix rhsSIGMABar) {
        this.rhsSIGMABar = rhsSIGMABar;
    }

    public double getSubjectiveBotLocationX0() {
        return subjectiveBotLocationX0;
    }

    public void setSubjectiveBotLocationX0(double subjectiveBotLocationX0) {
        this.subjectiveBotLocationX0 = subjectiveBotLocationX0;
    }

    public double getSubjectiveBotLocationY0() {
        return subjectiveBotLocationY0;
    }

    public void setSubjectiveBotLocationY0(double subjectiveBotLocationY0) {
        this.subjectiveBotLocationY0 = subjectiveBotLocationY0;
    }

    public double getSubjectiveBotOrientation0() {
        return subjectiveBotOrientation0;
    }

    public void setSubjectiveBotOrientation0(double subjectiveBotOrientation0) {
        this.subjectiveBotOrientation0 = subjectiveBotOrientation0;
    }
}
