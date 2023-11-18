// Mark McKelvy
// CMPS 523
// Final Project
// File: Logger.java
package agv.sim.cmps523;

import static java.lang.System.out;

import Jama.Matrix;
import java.io.*;
import java.util.Vector;


public class Logger {
    String dataDirectory;
    String agentDataFile;
    String testbedDataFile;
    String miscDataFile;

    Vector<Matrix> agentPoses;
    Vector<Matrix> testbedPoses;
    Vector<Double> time;

    Logger() {
        this.init();
    }

    static BufferedWriter new_buffered_writer(String directory, String filename) throws IOException {
        return new BufferedWriter(new FileWriter(new File(directory, filename)));
    }

    static void copy_file_to_directory(String file, String sdir, String ddir) {
        try {
            File src = new File(sdir, file);
            BufferedReader reader = new BufferedReader(new FileReader(src));
            File dest = new File(ddir, file);
            BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line + "\n");
                line = reader.readLine();
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void init() {
        dataDirectory = "src/data";
        agentDataFile = "agent_data.m";
        testbedDataFile = "testbed_data.m";
        miscDataFile = "misc_data.m";

        if (!(new File(dataDirectory)).mkdir()) {
            System.err.println("Unable to create directory: " + dataDirectory);
        }

        agentPoses = new Vector<>();
        testbedPoses = new Vector<>();
        time = new Vector<>();
        for (int i = 0; i < 3; i++)
            time.add(0.0);

        String src = "src/main/resources";
        Logger.copy_file_to_directory("plot_pose_error.m", src, dataDirectory);
        Logger.copy_file_to_directory("plot_pose_orientation_error.m", src, dataDirectory);
        Logger.copy_file_to_directory("plot_poses.m", src, dataDirectory);
    }

    void save_agent_pose(Matrix m) {
        agentPoses.add(m.copy());
    }

    void save_testbed_pose(Matrix m) {
        testbedPoses.add(m.copy());
    }

    void save_time(double tstamp) {
        time.add(tstamp);
    }

    void save_data() {
        out.print("Saving data..");
        try {
            BufferedWriter agent_data = Logger.new_buffered_writer(dataDirectory, agentDataFile);
            BufferedWriter testbed_data = Logger.new_buffered_writer(dataDirectory, testbedDataFile);
            BufferedWriter misc_data = Logger.new_buffered_writer(dataDirectory, miscDataFile);

            this.write_pose_vector_to_matlab_file(agent_data, agentPoses, "agent_poses");
            this.write_pose_vector_to_matlab_file(testbed_data, testbedPoses, "testbed_poses");
            this.write_array_to_matlab_file(misc_data, time, "time");

            Vector<Double> pose_diffs = new Vector<>();
            Vector<Double> pose_orient_diffs = new Vector<>();
            for (int i = 0; i < agentPoses.size(); i++) {
                pose_diffs.add(Utils.dist(agentPoses.elementAt(i), testbedPoses.elementAt(i)));
                final double degrees = Math.toDegrees(testbedPoses.elementAt(i).get(2, 0) - agentPoses.elementAt(i).get(2, 0));
                pose_orient_diffs.add(degrees);
            }
            this.write_array_to_matlab_file(agent_data, pose_diffs, "pose_error");
            this.write_array_to_matlab_file(agent_data, pose_orient_diffs, "pose_orientation_error");

            Vector<Double> objx = new Vector<>();
            Vector<Double> objy = new Vector<>();
            for (int i = 0; i < AGVsim.testbed.nuobjects(); i++) {
                objx.add(AGVsim.testbed.object_at(i).x());
                objy.add(AGVsim.testbed.object_at(i).y());
            }

            this.write_array_to_matlab_file(misc_data, objx, "objects_loc_x");
            this.write_array_to_matlab_file(misc_data, objy, "objects_loc_y");

            agent_data.close();
            testbed_data.close();
            misc_data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("done.");
    }

    void write_pose_vector_to_matlab_file(BufferedWriter writer, Vector<Matrix> poses, String var) throws IOException {
        writer.write("\n%begin auto output of array " + var + " (length: " + poses.size() + ")\n" + var + " = [\n");
        for (int i = 0; i < poses.size(); i++) {
            writer.write(" " + poses.elementAt(i).get(0, 0));
        }
        writer.write(" ; ");
        for (int i = 0; i < poses.size(); i++) {
            writer.write(" " + poses.elementAt(i).get(1, 0));
        }
        writer.write("\n]; %end auto output of array " + var);
    }

    void write_array_to_matlab_file(BufferedWriter writer, double[] array, String var) throws IOException {
        writer.write("\n%begin auto output of array " + var + " (length: " + array.length + ")\n" + var + " = [\n");
        for (final double v : array) {
            writer.write(" " + v);
        }
        writer.write("\n]; %end auto output of array " + var);
    }

    void write_array_to_matlab_file(BufferedWriter writer, Vector<Double> array, String var) throws IOException {
        writer.write("\n%begin auto output of array " + var + " (length: " + array.size() + ")\n" + var + " = [\n");
        for (int i = 0; i < array.size(); i++) {
            writer.write(" " + array.elementAt(i));
        }
        writer.write("\n]; %end auto output of array " + var);
    }

    void write_double_array_to_matlab_file(BufferedWriter writer, double[] array1, double[] array2, String var) throws IOException {
        if (array1.length != array2.length) {
            out.println("Error writing arrays.. not same size");
            return;
        }

        writer.write("\n%begin auto output of double array " + var + " (length: " + array1.length + ")\n" + var + " = [\n");
        for (final double v : array1) {
            writer.write(" " + v);
        }
        writer.write(" ; ");
        for (final double v : array2) {
            writer.write(" " + v);
        }
        writer.write("\n]; %end auto output of double array " + var);
    }
}
