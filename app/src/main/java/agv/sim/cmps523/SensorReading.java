// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorReading.java
package agv.sim.cmps523;

public class SensorReading {
	public double m_actual_angle;
	public double m_actual_range;
	public double m_believed_angle;
	public double m_believed_range;
	public int m_signature;
	public double m_x_actual_hit;
	public double m_y_actual_hit;
	public double m_x_believed_hit;
	public double m_y_believed_hit;
	
	SensorReading(){
		m_actual_angle = 0;
		m_actual_range = -1;
		m_believed_angle = 0;
		m_believed_range = -1;
		m_signature = -1;
		m_x_actual_hit = 0;
		m_y_actual_hit = 0;
		m_x_believed_hit = 0;
		m_y_believed_hit = 0;
	}
	
	SensorReading(
			double actual_angle, double actual_range,
			double believed_angle, double believed_range,
			int signature,
			double actual_x, double actual_y,
			double believed_x, double believed_y){
		this.m_actual_angle = actual_angle;
		this.m_actual_range = actual_range;
		this.m_believed_angle = believed_angle;
		this.m_believed_range = believed_range;
		this.m_signature = signature;
		this.m_x_actual_hit = actual_x;
		this.m_y_actual_hit = actual_y;		
		this.m_x_believed_hit = believed_x;
		this.m_y_believed_hit = believed_y;
	}
	
	public boolean is_valid(){
		return m_actual_range > -1;
	}
}
