// Mark McKelvy
// CMPS 523
// Final Project
// File: Particle.java

public class Particle {
	public double m_x;
	public double m_y;
	public double m_angle;
	
	Particle(double x, double y, double angle){
		m_x = x;
		m_y = y;
		m_angle = angle;
	}
	
	void set(double x, double y, double angle){
		m_x = x;
		m_y = y;
		m_angle = angle;		
	}
	
	double x(){
		return m_x;
	}
	
	double y(){
		return m_y;
	}
	
	double theta(){
		return m_angle;
	}
}
