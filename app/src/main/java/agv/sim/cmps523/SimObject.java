// Mark McKelvy
// CMPS 523
// Final Project
// File: SimObject.java
package agv.sim.cmps523;

public class SimObject {
		public double m_x,m_y;
		public double m_size;
		
		public SimObject(){
			m_x = 0.0;
			m_y = 0.0;
			m_size = 1.0;
		}
		public SimObject(double x, double y){
			this.m_x = x;
			this.m_y = y;
		}
		public SimObject(double x, double y, double size){
			this.m_x = x;
			this.m_y = y;
			this.m_size = size;
		}
		
		double x()
		{
			return m_x;
		}
		
		double y()
		{
			return m_y;
		}
}
