// Mark McKelvy
// CMPS 523
// Final Project
// File: Sensor.java
package agv.sim.cmps523;

import java.io.PrintStream;
import java.util.Vector;
import Jama.Matrix;


public class Sensor {
	
	static final PrintStream cout = System.out; // console out
	private Matrix m_pose;
	private int m_fov_degrees;
	private double m_max_range, m_min_range, m_range_resolution, m_angular_resolution;
	private Vector m_sensor_hits;
	private SensorReading m_sensor_readings[];
	private int m_beams_completed;	
	Matrix m_noise;
	double sigma_hit;
	double lambda_short;
		
	Sensor(){
		m_pose = new Matrix(3,1);
		m_fov_degrees = 180;
		m_min_range = 0;
		m_max_range = 500;
		m_sensor_hits = new Vector();
		m_range_resolution = 1.0;
		m_angular_resolution = Math.toRadians(5.0);
		init_readings();
	}
	
	void normalize_noise(){
		double sum_squares = 0.0;
		double length;
		for (int i = 0; i < 4; i++){
			m_noise.set(0, i, SensorControlPanel.get_sensor_noise_probability(i));
			sum_squares += Utils.square(m_noise.get(0, i));
		}
		if (sum_squares <= 0.0)
			return;
		length = Math.sqrt(sum_squares);
		for (int i = 0; i < 4; i++){
			m_noise.set(0, i, m_noise.get(0, i) / length);
		}
		//m_noise.print(20,4);		
	}
	
	void init_readings(){
		m_sensor_readings = new SensorReading[(int)((m_fov_degrees)/Math.toDegrees(m_angular_resolution) + 1)];
		for (int i = 0; i < m_sensor_readings.length; i++)
			m_sensor_readings[i] = new SensorReading();
		m_noise = Matrix.identity(1, 4);
		for (int i = 0; i < 4; i++){
			m_noise.set(0, i, SensorControlPanel.get_sensor_noise_probability(i));
		}
		sigma_hit = SensorControlPanel.get_sigma_hit();
		lambda_short = SensorControlPanel.get_lambda_short();
		normalize_noise();
	}
	
	void set_pose (Matrix pose){
		if (pose.getRowDimension() == 3 &&
			pose.getColumnDimension() == 1){
				m_pose = pose;
				init_readings();
		}
		else
			cout.println("Error: Couldn't set sensor pose");
	}
	
	Matrix get_pose(){
		return m_pose;
	}
	
	double get_x_position(){
		return m_pose.get(0, 0);
	}
	
	double get_y_position(){
		return m_pose.get(1, 0);
	}
	
	double get_orientation(){
		return m_pose.get(2,0);
	}
	
	void set_position(double x, double y){
		m_pose.set(0, 0, x);
		m_pose.set(1, 0, y);
		init_readings();
	}
	
	void set_orientation(double orient){
		if (orient > Math.PI*2)
			orient -= Math.PI*2;
		else if (orient < -Math.PI*2)
			orient += Math.PI*2;
		m_pose.set(2, 0, orient);
		init_readings();
	}
	
	void set_fov(int fov_degrees){
		if (fov_degrees > 0 && fov_degrees <= 360)
		{
			m_fov_degrees = fov_degrees;
			m_sensor_hits.clear();
			init_readings();
		}
	}
	
	void set_angular_resolution(double ang_res_degrees){
		if (ang_res_degrees > 0 && ang_res_degrees <= m_fov_degrees)
		{
			m_angular_resolution = Math.toRadians(ang_res_degrees);
			m_sensor_hits.clear();
			init_readings();
		}
	}
	
	void set_max_range(double max_range){
		if (max_range > m_min_range){
			this.m_max_range = max_range;			
		}
	}
	
	double get_max_range(){
		return m_max_range;
	}
	
	void set_min_range(double min_range){
		if (min_range >= 0.0 &&
			min_range < m_max_range)
			m_min_range = min_range;
	}
	
	// Page 155
	double p_hit(double measured_range, double actual_range){
		double p = 0.0;
		if (measured_range >= 0 &&
				measured_range <= m_max_range &&
				sigma_hit > 0.0){
			// p = Utils.gaussian(actual_range, sigma_hit);
			p = 1 / (Math.sqrt(2 * Math.PI * Utils.square(sigma_hit))) * 
				Math.exp(-0.5 * Utils.square(measured_range - actual_range) / Utils.square(sigma_hit));
		}
		
		return p;
	}
	
	// Page 156
	double p_short(double measured_range, double actual_range){
		double p = 0.0;
		if (measured_range >= 0 &&
				measured_range <= actual_range &&
				lambda_short > 0.0){
			double eta = 1.0 / (1 - Math.exp(-lambda_short * actual_range));
			p = eta * lambda_short * Math.exp(-lambda_short * measured_range);	
		}
		
		return p;
	}
	
	// Page 156
	double p_max(double measured_range){
		double p = 0.0;
		if (measured_range == m_max_range)
			p = 1.0;
		
		return p;
	}
	
	// Page 157
	double p_rand(double measured_range){
		double p = 0.0;
		if (measured_range <= m_max_range)
			p = 1.0 / m_max_range;
		
		return 0.0;
	}
	
	void sense(Testbed testbed){
		double actual_beam;
		double believed_beam;
		actual_beam = get_orientation() - Math.toRadians(m_fov_degrees / 2.0);
		believed_beam = AGVsim.m_agent.get_sensor_orientation() - Math.toRadians(m_fov_degrees / 2.0);
		//cout.println("Sensor: bot orientation vs. sensor orientation: " + Math.toDegrees(get_orientation()) + " " + Math.toDegrees(AGVsim.m_agent.get_sensor_orientation()));
		
		m_sensor_hits.clear();
		m_beams_completed = 0;
		for (int beam_num = 0; beam_num < m_sensor_readings.length; beam_num++){
			new ProcessBeam(testbed, actual_beam, believed_beam, beam_num);
			actual_beam += m_angular_resolution;
			believed_beam += m_angular_resolution;
		}	
	}	
	
	private class ProcessBeam{
		double actual_angle, believed_angle, range, believed_range;
		double actual_beam_x = 0.0, actual_beam_y = 0.0;
		double believed_beam_x = 0.0, believed_beam_y = 0.0;
		double obj_x, obj_y;
		boolean hit;
		Matrix noise_matrix;
		
		ProcessBeam(Testbed testbed, double actual_beam, double believed_beam, int beam_index){
			noise_matrix = Matrix.identity(4, 1);
			actual_angle = actual_beam;
			believed_angle = believed_beam;
			hit = false;
			for (range = m_min_range; range <= m_max_range; range += m_range_resolution){
				if (hit)
					break;
				actual_beam_x = get_x_position() + range * Math.cos(actual_angle);
				actual_beam_y = get_y_position() + range * Math.sin(actual_angle);
				for (int o = 0; o < testbed.num_objects(); o++){
					obj_x = testbed.object_at(o).m_x;
					obj_y = testbed.object_at(o).m_y;
					if (1.0 >= Math.sqrt(Utils.square(actual_beam_x - obj_x) + Utils.square(actual_beam_y - obj_y))){
						hit = true;
						
						noise_matrix.set(0, 0, p_hit(range, range));
						noise_matrix.set(1, 0, p_short(range, range));
						noise_matrix.set(2, 0, p_max(range));
						noise_matrix.set(3, 0, p_rand(range));
						//m_noise.print(20,3);
						//noise_matrix.transpose().print(20,3);
						double p = (m_noise.times(noise_matrix)).get(0, 0);
						//cout.println("p=" + p);
						
						believed_range = range * (1-p);
						believed_beam_x = (AGVsim.m_agent.get_sensor_x_position() + believed_range * Math.cos(believed_angle));
						believed_beam_y = (AGVsim.m_agent.get_sensor_y_position() + believed_range * Math.sin(believed_angle));
						//cout.println("Sensor: actual angle vs believed angle: " + Math.toDegrees(actual_angle) + " " + Math.toDegrees(believed_angle));
						m_sensor_hits.add(new SensorReading(actual_angle-get_orientation(), range, believed_angle-AGVsim.m_agent.get_sensor_orientation(), believed_range, o, 
								testbed.object_at(o).m_x, testbed.object_at(o).m_y, believed_beam_x, believed_beam_y));
						m_sensor_readings[beam_index] = new SensorReading(actual_angle-get_orientation(), range, believed_angle-AGVsim.m_agent.get_sensor_orientation(), believed_range, o, 
								testbed.object_at(o).m_x, testbed.object_at(o).m_y, believed_beam_x, believed_beam_y);
						break;
					}
				}	
			}
			if (!hit){
				actual_beam_x = get_x_position() + range * Math.cos(actual_angle);
				actual_beam_y = get_y_position() + range * Math.sin(actual_angle);
				believed_beam_x = AGVsim.m_agent.get_sensor_x_position() + range * Math.cos(believed_angle);
				believed_beam_y = AGVsim.m_agent.get_sensor_y_position() + range * Math.sin(believed_angle);
				m_sensor_readings[beam_index] =  new SensorReading(actual_angle-get_orientation(), range, believed_angle-AGVsim.m_agent.get_sensor_orientation(), range, -1, 
						actual_beam_x, actual_beam_y, believed_beam_x, believed_beam_y);
			}
		}
	}
	
	Vector get_hits(){
		return m_sensor_hits;
	}
	
	SensorReading[] get_readings(){
		return m_sensor_readings;
	}
}