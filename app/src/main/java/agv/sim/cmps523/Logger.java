// Mark McKelvy
// CMPS 523
// Final Project
// File: Logger.java
package agv.sim.cmps523;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import Jama.Matrix;


public class Logger {
	static final PrintStream cout = System.out; // console out
	
	String data_directory;
	String agent_data_file;
	String testbed_data_file;
	String misc_data_file;
	
	Vector agent_poses;
	Vector testbed_poses;
	Vector pose_error;
	Vector pose_orientation_error;
	Vector time;
	
	Logger(){
		this.init();
	}
	
	void init(){
		data_directory = "src/data";
		agent_data_file = "agent_data.m";
		testbed_data_file = "testbed_data.m";
		misc_data_file = "misc_data.m";
		
		(new File(data_directory)).mkdir();
		
		agent_poses = new Vector();
		testbed_poses = new Vector();
		time = new Vector();
		for (int i = 0; i < 3; i++)
			time.add(0.0);
		
		String src = "src/main/resources";
		Logger.copy_file_to_directory("plot_pose_error.m", src, data_directory);
		Logger.copy_file_to_directory("plot_pose_orientation_error.m", src, data_directory);
		Logger.copy_file_to_directory("plot_poses.m", src, data_directory);		
	}
	
	void save_agent_pose(Matrix m){
		agent_poses.add(m.copy());
	}
	
	void save_testbed_pose(Matrix m){
		testbed_poses.add(m.copy());
	}
	
	void save_time(double tstamp){
		time.add(tstamp);
	}
	
	void save_data(){
		cout.print("Saving data..");
		try{
			BufferedWriter agent_data = Logger.new_buffered_writer(data_directory, agent_data_file);
			BufferedWriter testbed_data = Logger.new_buffered_writer(data_directory, testbed_data_file);
			BufferedWriter misc_data = Logger.new_buffered_writer(data_directory, misc_data_file);
			
			this.write_pose_vector_to_matlab_file(agent_data, agent_poses, "agent_poses");
			this.write_pose_vector_to_matlab_file(testbed_data, testbed_poses, "testbed_poses");
			this.write_array_to_matlab_file(misc_data, time, "time");
			
			Vector pose_diffs = new Vector();
			Vector pose_orient_diffs = new Vector();
			for (int i = 0; i < agent_poses.size(); i++){
				pose_diffs.add(Utils.dist(agent_poses.elementAt(i), testbed_poses.elementAt(i)));
				pose_orient_diffs.add(Math.toDegrees(((Matrix) testbed_poses.elementAt(i)).get(2, 0) - ((Matrix) agent_poses.elementAt(i)).get(2, 0)));
			}
			this.write_array_to_matlab_file(agent_data, pose_diffs, "pose_error");
			this.write_array_to_matlab_file(agent_data, pose_orient_diffs, "pose_orientation_error");
			
			Vector objx = new Vector();
			Vector objy = new Vector();
			for (int i = 0; i < AGVsim.m_testbed.num_objects(); i++){
				objx.add(AGVsim.m_testbed.object_at(i).x());
				objy.add(AGVsim.m_testbed.object_at(i).y());
			}
			
			this.write_array_to_matlab_file(misc_data, objx, "objects_loc_x");
			this.write_array_to_matlab_file(misc_data, objy, "objects_loc_y");
			
			agent_data.close();
			testbed_data.close();
			misc_data.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		cout.println("done.");
	}
	
	static BufferedWriter new_buffered_writer(String directory, String filename) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter (new File(directory, filename)));
		return writer;
	}
	
	static void copy_file_to_directory(String file, String sdir, String ddir){
		try{
			File src = new File(sdir, file);
			BufferedReader reader = new BufferedReader(new FileReader(src));
			File dest = new File(ddir, file);
			BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
			String line = reader.readLine();
			while (line != null){
				writer.write(line + "\n");
				line = reader.readLine();
			}
			writer.close();
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	void write_pose_vector_to_matlab_file(BufferedWriter writer, Vector poses, String var) throws IOException{
		writer.write("\n%begin auto output of array " + var + " (length: " + poses.size() + ")\n" + var + " = [\n");
		for (int i = 0; i < poses.size(); i++){
			writer.write(" " + ((Matrix) poses.elementAt(i)).get(0, 0));
		}
		writer.write(" ; ");
		for (int i = 0; i < poses.size(); i++){
			writer.write(" " + ((Matrix) poses.elementAt(i)).get(1, 0));
		}
		writer.write("\n]; %end auto output of array " + var);		
	}
	
	void write_array_to_matlab_file(BufferedWriter writer, double array[], String var) throws IOException{
		writer.write("\n%begin auto output of array " + var + " (length: " + array.length + ")\n" + var + " = [\n");
		for (int i = 0; i < array.length; i++){
			writer.write(" " + array[i]);
		}
		writer.write("\n]; %end auto output of array " + var);		
	}
	
	void write_array_to_matlab_file(BufferedWriter writer, Vector array, String var) throws IOException{
		writer.write("\n%begin auto output of array " + var + " (length: " + array.size() + ")\n" + var + " = [\n");
		for (int i = 0; i < array.size(); i++){
			writer.write(" " + ((Double)(array.elementAt(i))).doubleValue());
		}
		writer.write("\n]; %end auto output of array " + var);		
	}
	
	void write_double_array_to_matlab_file(BufferedWriter writer, double array1[], double array2[], String var) throws IOException{
		if (array1.length != array2.length){
			cout.println("Error writing arrays.. not same size");
			return;
		}
		
		writer.write("\n%begin auto output of double array " + var + " (length: " + array1.length + ")\n" + var + " = [\n");
		for (int i = 0; i < array1.length; i++){
			writer.write(" " + array1[i]);
		}
		writer.write(" ; ");
		for (int i = 0; i < array2.length; i++){
			writer.write(" " + array2[i]);
		}
		writer.write("\n]; %end auto output of double array " + var);		
	}
}
