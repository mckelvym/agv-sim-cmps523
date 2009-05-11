// Mark McKelvy
// CMPS 523
// Final Project
// File: Agent.java 

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.*;

import Jama.*;

public class Agent {
	static final PrintStream cout = System.out; // console out
	
	private Matrix m_pose;
	private Matrix m_sensor_pose;
	private Matrix m_control;
	private Matrix SIGMA;
	private Matrix lhsSIGMA_bar, rhsSIGMA_bar;
	private double m_subjective_bot_location_x0 = 180; // initial subjective bot loc x
	private double m_subjective_bot_location_y0 = 200; // initial subjective bot loc y
	private double m_subjective_bot_orientation0 = Math.PI/2;
	Sensor m_sensor;
	boolean m_enable_correction;
	
	int m_num_particles = 100;
	Particle m_particles[];

	// agent noise
	double m_mu_noise = 0.0; // default value
	double m_a1_noise = 0.0; // default value
	double m_a2_noise = 0.0; // default value
	double m_a3_noise = 0.0; // default value
	double m_a4_noise = 0.0; // default value
	double m_a5_noise = 0.0; // default value
	double m_a6_noise = 0.0; // default value
	Matrix Qt;
	boolean m_config_orientation = false;
	
	public Agent() {
		m_pose = new Matrix(3,1);
		m_sensor_pose = new Matrix(3,1);
		m_control = new Matrix(2,1);
		SIGMA = new Matrix(3,3);
		lhsSIGMA_bar = rhsSIGMA_bar = SIGMA;
		Qt = new Matrix(3,3);
		initialize_subjective_bot_pose();
		initialize_agent_noise();
		initialize_particles();
		m_enable_correction = true;
	}
	
	void initialize_particles(){
		m_num_particles = ParticleDialog.number_particles;
		m_particles = new Particle[m_num_particles];
		//cout.println("Initting " + m_num_particles + " particles.");
		for (int i = 0; i < m_num_particles; i++){
			m_particles[i] = new Particle(
					Utils.gaussian(m_subjective_bot_location_x0, 1),
					Utils.gaussian(m_subjective_bot_location_y0, 1),
					Utils.gaussian(m_subjective_bot_orientation0, Math.toRadians(1))
					);
			/*
			m_particles[i] = new Particle(
					Math.random()*1600,
					Math.random()*800,
					Math.random()*2*Math.PI
					);
					*/
		}
	}
	
	void initialize_agent_noise() {
		m_a1_noise = NoiseControlPanel.get_alpha_noise(1);
		m_a2_noise = NoiseControlPanel.get_alpha_noise(2);
		m_a3_noise = NoiseControlPanel.get_alpha_noise(3);
		m_a4_noise = NoiseControlPanel.get_alpha_noise(4);
		for (int i = 0; i <= 2; i++){
			Qt.set(i, i, NoiseControlPanel.get_sensor_noise(i+1));
			String index;
			if (i == 0)
				index = "range";
			else if (i == 1)
				index = "bearing";
			else
				index = "signature";
			//cout.println("Agent: Qt noise sigma[" + index + "]^2 = " + Qt.get(i,i));
		}	
		if (AGVsim.algorithm == 1){
			//cout.println("Agent: alpha noise: " + m_a1_noise + " " + m_a2_noise + " " + m_a3_noise + " " + m_a4_noise);
		}
		else{
			m_a5_noise = NoiseControlPanel.get_alpha_noise(5);
			m_a6_noise = NoiseControlPanel.get_alpha_noise(6);
			//cout.println("Agent: alpha noise: " + m_a1_noise + " " + m_a2_noise + " " + m_a3_noise + " " + m_a4_noise + " " + m_a5_noise + " " + m_a6_noise );
		}		
	}

	void initialize_subjective_bot_pose() {
		set_position(m_subjective_bot_location_x0, m_subjective_bot_location_y0);
		set_orientation(m_subjective_bot_orientation0);
		m_control.set(0, 0, 0);
		m_control.set(1, 0, 0);
		SIGMA.set(0, 0, 0.0);	SIGMA.set(0, 1, 0.0);	SIGMA.set(0, 2, 0.0);
		SIGMA.set(1, 0, 0.0);	SIGMA.set(1, 1, 0.0);	SIGMA.set(1, 2, 0.0);
		SIGMA.set(2, 0, 0.0);	SIGMA.set(2, 1, 0.0);	SIGMA.set(2, 2, 0.0);
		
		AGVsim.m_logger.save_agent_pose(m_pose);
	}
	
	void ekf(){
		AGVsim.m_testbed.move(m_control);
		
		double dt = AGVsim.m_engine.m_delta_t;		// delta time
		double v = (m_control.get(0, 0)); 			// v from robot motion - translational velocity
		double w = (m_control.get(1, 0)); 			// omega from robot motion - rotataional velocity
		double v_over_w = (w != 0.0)? v/w : 0;
		double w_sqr = w*w;
		double theta = get_orientation(); // LINE 2
		double dtheta = w*dt;
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
		Vt = new Matrix(3,2);
		// column 1
		Vt.set(0, 0, -(sin_theta + sin_theta_dtheta)/w);	
		Vt.set(1, 0,  (cos_theta - cos_theta_dtheta)/w);		
		Vt.set(2, 0, 0);
		// column 2
		Vt.set(0, 1, 
				(v*(sin_theta - sin_theta_dtheta))/w_sqr + 
				(v*cos_theta_dtheta*dt)/w				
			);
		Vt.set(1, 1, 
				-(v*(cos_theta - cos_theta_dtheta))/w_sqr + 
				 (v*sin_theta_dtheta*dt)/w				
			);
		Vt.set(2, 1, dt);
		VtT = Vt.transpose();
		
		// LINE 5, motion noise covariance matrix in control space
		Mt = Matrix.identity(2, 2);
		//cout.println("Agent: alpha noise: " + m_a1_noise + " " + m_a2_noise + " " + m_a3_noise + " " + m_a4_noise);
		Mt.set(0, 0, Utils.square(m_a1_noise*v + m_a2_noise*w));	
		Mt.set(1, 1, Utils.square(m_a3_noise*v + m_a4_noise*w));
		
		// LINE 6, motion update from motion model
		Mut_update = new Matrix(3,1);
		Mut_update.set(0, 0, -(v_over_w * sin_theta) + (v_over_w * sin_theta_dtheta));
		Mut_update.set(1, 0,  (v_over_w * cos_theta) - (v_over_w * cos_theta_dtheta));
		Mut_update.set(2, 0, dtheta);
		
		Mut_bar = m_pose;
		Mut_bar = Mut_bar.plus(Mut_update);
		Mut_bar.set(2, 0, Utils.clamp_angle(Mut_bar.get(2, 0)));
		//cout.println("Believed Pose: " + Mu_bar.get(0,0) + " " + Mu_bar.get(1, 0) + " " + Math.toDegrees(Mu_bar.get(2, 0)));

		// LINE 7, motion covariance matrix update from motion model
		// lhs of plus: predicted belief based on Jacobian control actions and SIGMA
		// rhs of plus: mapping between motion noise in control space to motion noise in state space. 
		lhsSIGMA_bar = Gt.times(SIGMA.times(GtT));
		rhsSIGMA_bar = Vt.times(Mt.times(VtT));
		
		SIGMAt_bar = lhsSIGMA_bar.plus(rhsSIGMA_bar);
		
		m_sensor_pose = Mut_bar;
		if (m_sensor != null)
			m_sensor.sense(AGVsim.m_testbed);			
		
		// LINE 8, covariance matrix for error in laser range finder
		// setup in function initialize_agent_noise();
				
		if (m_sensor != null && m_enable_correction){
			//cout.println("Correction Step");

			// LINE 9, for each actual observation...
			SensorReading z_t_i;
			Vector z_t = m_sensor.get_hits();
			for (int i = 0; i < z_t.size(); i++){
				//cout.println("Processing hit..");
				
				// LINE 10, unique identifier of landmark from correspondence table
				z_t_i = (SensorReading) z_t.elementAt(i);
				int j = z_t_i.m_signature;
				
				// LINE 11, simplicity calculation: actual position of landmark minus robot believed position
				double mjx_mutx = AGVsim.m_testbed.object_at(j).m_x - Mut_bar.get(0, 0);
				double mjy_muty = AGVsim.m_testbed.object_at(j).m_y - Mut_bar.get(1, 0);
				double q = Utils.square(mjx_mutx) + Utils.square(mjy_muty);
				//cout.println("xdist=" + mjx_mutx + " ydist=" + mjy_muty + " range=" + Math.sqrt(q));
				
				// LINE 12, z_hat is predicted observation based on robot's believed position
				double sqrt_q = Math.sqrt(q);
				Matrix z = new Matrix(3,1);
				Matrix z_t_hat = new Matrix(3,1);
				z.set(0, 0, z_t_i.m_actual_range);
				z.set(1, 0, z_t_i.m_actual_angle);
				z.set(2, 0, z_t_i.m_signature);
				z_t_hat.set(0, 0, sqrt_q);
				z_t_hat.set(1, 0, Math.atan2(mjy_muty, mjx_mutx) - Mut_bar.get(2, 0));
				z_t_hat.set(2, 0, z_t_i.m_signature);
				if (z_t_hat.get(1, 0) > Math.PI/2){
					//cout.print("OLD = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
					z_t_hat.set(1, 0, Utils.clamp_angle_within_pi_over_two(z_t_hat.get(1, 0)));
					//cout.println("  NEW = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
					
				}
				//cout.println("z    range=" + z.get(0,0) + "\tangle=" + Math.toDegrees(z.get(1, 0)) + "\tsig=" + z.get(2, 0));
				//cout.println("zhat range=" + z_t_hat.get(0,0) + "\tangle=" + Math.toDegrees(z_t_hat.get(1, 0)) + "\tsig=" + z_t_hat.get(2, 0));
				//cout.println("atan2=" + Math.toDegrees(Math.atan2(mjy_muty, mjx_mutx)) + " Mut_bar=" + Math.toDegrees(Mut_bar.get(2, 0)));
				
				//cout.println("z angle=" + Math.round(Math.toDegrees(z.get(1, 0))) 
				//		+ " zhat angle=" + Math.round(Math.toDegrees(z_t_hat.get(1, 0))) 
				//		+ " atan2=" + Math.round(Math.toDegrees(Math.atan2(mjy_muty, mjx_mutx))) 
				//		+ " Mut_bar=" + Math.round(Math.toDegrees(Mut_bar.get(2, 0)))
				//		+ " mjx=" + mjx_mutx
				//		+ " mjy=" + mjy_muty);
				
				// LINE 13, H is Jacobian wrt to pose (x,y,theta) taken of h (measurement model)
				Matrix Ht = new Matrix(3,3);
				Matrix HtT;
				Ht.set(0, 0, -mjx_mutx/sqrt_q);	Ht.set(0, 1, -mjy_muty/sqrt_q);	Ht.set(0, 2, 0);
				Ht.set(1, 0, mjy_muty/q);		Ht.set(1, 1, -mjx_mutx/q);		Ht.set(1, 2, -1);
				Ht.set(2, 0, 0);				Ht.set(2, 1, 0);				Ht.set(2, 2, 0);
				HtT = Ht.transpose();
				
				// LINE 14, S is the sum of two covariance matrices
				Matrix St = new Matrix(3,3);
				Matrix St_inv;
				St = Ht.times(SIGMAt_bar.times(HtT)).plus(Qt);
				if (St.det() == 0){
					St.print(10, 3);
					cout.println("Agent: S is a singular matrix, skipping correction " + i + ".");
					continue;
				}	
				St_inv = St.inverse();
							
				// LINE 15, Kalman gain
				Matrix Kt = new Matrix(3,3);
				Kt = SIGMAt_bar.times(HtT.times(St_inv));
				
				// LINE 16, innovation calculation - difference between observed & predicted position
				Mut_bar = Mut_bar.plus(Kt.times(z.minus(z_t_hat)));
				Mut_bar.set(2, 0, Utils.clamp_angle(Mut_bar.get(2, 0)));
				
				// LINE 17
				SIGMAt_bar = Matrix.identity(3,3).minus(Kt.times(Ht)).times(SIGMAt_bar);
			} // LINE 18
		}
		
		// LINE 19
		m_pose = Mut_bar;
		m_sensor_pose = m_pose;
		
		// LINE 20
		SIGMA = SIGMAt_bar;
	}
	
	void mcl(){
		AGVsim.m_testbed.move(m_control);
		m_sensor.sense(AGVsim.m_testbed);
		SensorReading z_t_i;
		Vector z_t = m_sensor.get_hits();
		z_t_i = closest_hit(z_t);
		double weights[] = new double[m_num_particles];
		double Minv = 1.0 / (double)m_num_particles;
		
		Particle m_new_particles[] = new Particle[m_num_particles];
		for (int m = 0; m < m_num_particles; m++){
			m_new_particles[m] = sample_motion_model_velocity(m_control, m_particles[m]);
			weights[m] = 0.0;
			if (z_t.size() >= 1)
				weights[m] = landmark_model_known_correspondence(
							z_t_i, -1, m_new_particles[m], AGVsim.m_testbed);
			else weights[m] = Minv;
			//cout.println("Weight[" + m + "]=" + weights[m]);
		}
		if (this.m_enable_correction)
			m_particles = low_variance_sampler(m_new_particles, weights);
		
		m_pose = new Matrix(3,1);
		for (int m = 0; m < m_num_particles; m++){
			m_pose.set(0, 0, m_pose.get(0,0) + m_particles[m].x());
			m_pose.set(1, 0, m_pose.get(1,0) + m_particles[m].y());
			m_pose.set(2, 0, m_pose.get(2,0) + m_particles[m].theta());
		}
		m_pose.set(0, 0, m_pose.get(0,0) / m_num_particles);
		m_pose.set(1, 0, m_pose.get(1,0) / m_num_particles);
		m_pose.set(2, 0, Utils.clamp_angle(m_pose.get(2,0) / m_num_particles));
	}
	
	SensorReading closest_hit(Vector hits){
		SensorReading sr = new SensorReading();
		double range = m_sensor.get_max_range();
		for (int i = 0; i < hits.size(); i++){
			if (((SensorReading)hits.elementAt(i)).m_actual_range < range)
				sr = (SensorReading)hits.elementAt(i);
		}
		return sr;
	}
	
	// Page 110
	// chi_t is the particle set
	// weights are the associated weights for each particle
	Particle[] low_variance_sampler(Particle chi_t[], double weights[]){
		Particle m_new_particles[] = new Particle[m_num_particles];
		double Minv = 1.0 / (double)m_num_particles;
		double r = Utils.rand(0.0, Minv);
		double c = weights[0];
		int i = 1;
		for (int m = 1; m <= m_num_particles; m++){
			double U = r + (m-1) * Minv;
			while (U > c && i < m_num_particles){
				//cout.println("U=" + U + " c=" + c + " m=" + m + " r=" + r + " i=" + i);
				i++;
				c = c + weights[i-1];
			}
			//cout.println("Adding particle " + i + " to the set.");
			m_new_particles[m-1] = chi_t[i-1];
		}
		return m_new_particles;
	}
	
	// Page 179
	// f is an observed feature
	// c is unused and represents correspondence variable - true identity of the feature
	// x_t is robot pose
	// m is map with objects
	double landmark_model_known_correspondence(SensorReading f, int c, Particle x_t, Testbed m){
		int j = f.m_signature; // LINE 2 j = c_i_t
		SimObject o = m.object_at(j);
		//cout.println("BOT (X,Y)=(" + x_t.get(0,0) + ", " + x_t.get(1,0) + ") OBJ (X,Y)=(" + o.x() + ", " + o.y() + ")");
		double r_hat = Math.sqrt(
					Utils.square(o.x() - x_t.x()) 
					+ Utils.square(o.y() - x_t.y())); // LINE 3
		double phi_hat = Math.atan2(o.y() - x_t.y(), o.x() - x_t.x()) - x_t.theta(); // LINE 4
		if (phi_hat > Math.PI/2){
			//cout.print("OLD = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
			phi_hat = Utils.clamp_angle_within_pi_over_two(phi_hat);
			//cout.println("  NEW = " + Math.round(Math.toDegrees(z_t_hat.get(1, 0))));
			
		}
		
		// LINE 5
		// f.m_actual_range represents r_i_t
		// Qt.get(0, 0) represents sigma_r, for example.
		//cout.println("p1=" + Utils.prob(f.m_actual_range - r_hat, Math.sqrt(Qt.get(0, 0))) 
		//		+ "r_hat=" + r_hat + " r_act=" + f.m_actual_range  + "Q_sig_r=" + Math.sqrt(Qt.get(0, 0))
		//		+ " p2=" + Utils.prob(f.m_actual_angle - phi_hat, Math.sqrt(Qt.get(1, 1))) 
		//		+ " p3=" + Utils.prob(0, Math.sqrt(Qt.get(2, 2))));
		double q = Utils.prob(f.m_actual_range - r_hat, Math.sqrt(Qt.get(0, 0))) *
			Utils.prob(f.m_actual_angle - phi_hat, Math.sqrt(Qt.get(1, 1))) *
			Utils.prob(0, Math.sqrt(Qt.get(2, 2))); // prob (s_i_t - s_j, sigma_s) ??
		return q;
	}
	
	// Page 124 - give noise to a particle
	// u_t is a control action with v and omega (w)
	// x_t_minus_one is a particle which represents state of bot
	Particle sample_motion_model_velocity(Matrix u_t, Particle x_t_minus_one){
		double v = u_t.get(0, 0);
		double w = u_t.get(1, 0);
		double v_abs = Math.abs(v);
		double w_abs = Math.abs(w);
		double v_hat = v + sample(m_a1_noise*v_abs + m_a2_noise*w_abs); // LINE 2
		double w_hat = w + sample(m_a3_noise*v_abs + m_a4_noise*w_abs); // LINE 3
		double gamma_hat = sample(m_a5_noise*v_abs + m_a6_noise*w_abs); // LINE 4
		double theta = x_t_minus_one.m_angle;
		double v_hat_over_w_hat = v_hat / w_hat;
		double theta_dtheta = theta + w_hat * AGVsim.m_engine.m_delta_t;
		// LINES 5-7
		Particle x_t = new Particle(
				x_t_minus_one.m_x - v_hat_over_w_hat*Math.sin(theta) + v_hat_over_w_hat*Math.sin(theta_dtheta),
				x_t_minus_one.m_y + v_hat_over_w_hat*Math.cos(theta) - v_hat_over_w_hat*Math.cos(theta_dtheta),
				theta_dtheta + gamma_hat * AGVsim.m_engine.m_delta_t
				);
		
		return x_t; // LINE 8
	}
	
	// Give a randomly distributed sample from -val to val
	double sample(double val){
		return sample_triangular_distribution(val);
	}
	
	// Page 124 - give a sample from a normal distribution. More costly than sampling triangular distribution
	double sample_normal_distribution(double b){
		double sum = 0;
		for (int i = 0; i < 12; i++)
			sum += Utils.rand(-b, b);
		return 1.0/2.0 * sum;
	}
	
	// Page 124 - give a sample from a triangle distribution which is usually ok to approximate a sample from a normal distribution
	double sample_triangular_distribution(double b){
		final double sqrt_six_over_two = Math.sqrt(6) / 2.0;
		return sqrt_six_over_two * (Utils.rand(-b, b) + Utils.rand(-b, b));
	}
	
	void act_and_observe() {
		if (AGVsim.algorithm == 1)
			ekf();
		if (AGVsim.algorithm == 2)
			mcl();
		AGVsim.m_logger.save_agent_pose(m_pose);
	}			
	
	void set_sensor(Sensor sensor){
		m_sensor = sensor;
	}
	
	double get_x_position(){
		return m_pose.get(0, 0);
	}
	
	double get_y_position(){
		return m_pose.get(1, 0);
	}
	
	double get_sensor_x_position(){
		return m_sensor_pose.get(0, 0);
	}
	
	double get_sensor_y_position(){
		return m_sensor_pose.get(1, 0);
	}
	
	double get_initial_x_position(){
		return m_subjective_bot_location_x0;
	}
	
	double get_initial_y_position(){
		return m_subjective_bot_location_y0;
	}
	
	double get_orientation(){
		return m_pose.get(2,0);
	}
	
	double get_sensor_orientation(){
		return m_sensor_pose.get(2,0);
	}
	
	double get_initial_orientation(){
		return m_subjective_bot_orientation0;
	}
	
	void set_initial_position(double x, double y){
		m_subjective_bot_location_x0 = x;
		m_subjective_bot_location_y0 = y;
		m_sensor_pose.set(0, 0, x);
		m_sensor_pose.set(1, 0, y);
	}
	
	void set_position(double x, double y){
		m_pose.set(0, 0, x);
		m_pose.set(1, 0, y);
		m_sensor_pose.set(0, 0, x);
		m_sensor_pose.set(1, 0, y);	
}
	
	void set_orientation(double orient){
		if (orient > Math.PI*2)
			orient -= Math.PI*2;
		else if (orient < -Math.PI*2)
			orient += Math.PI*2;
		m_pose.set(2, 0, orient);
		m_sensor_pose.set(2, 0, orient);
	}
	
	void set_initial_orientation(double orient){
		m_subjective_bot_orientation0 = orient;
		m_sensor_pose.set(2, 0, orient);
	}
	
	void set_translational_velocity(double cm_sec){
		if (cm_sec > 0.0)
			m_control.set(0, 0, cm_sec);
	}
	
	double get_translational_velocity(){
		return m_control.get(0,0);
	}
	
	void set_rotational_velocity(double rad_sec){
		m_control.set(1, 0, rad_sec);
	}
	
	double get_rotational_velocity(){
		return m_control.get(1,0);
	}
	
	void print_mat(Matrix m){
		for (int i = 0; i < m.getRowDimension(); i++){
			for (int j = 0; j < m.getColumnDimension(); j++){
				cout.print(m.get(i,j) + "\t");
			}
			cout.println("\n");
		}
	}

	Matrix get_covar_mat(int index){
		switch(index){
		case 0:
			return rhsSIGMA_bar;
		case 1:
			return lhsSIGMA_bar;
		case 2:
			return SIGMA;
		default:
			return SIGMA;
		}
	}
	



}
