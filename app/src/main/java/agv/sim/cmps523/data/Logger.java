// Mark McKelvy
// CMPS 523
// Final Project
// File: Logger.java
package agv.sim.cmps523.data;

import static agv.sim.cmps523.math.MathUtil.dist;
import static java.lang.Math.toDegrees;
import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import Jama.Matrix;
import agv.sim.cmps523.Values;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


public class Logger {
    private final Values values;
    private final String dataDirectory;
    private final String agentDataFile;
    private final String testbedDataFile;
    private final String miscDataFile;
    private Vector<Matrix> agentPoses;
    private Vector<Matrix> testbedPoses;
    private Vector<Double> timestamps;

    public Logger(Values values) {
        this.values = requireNonNull(values);
        dataDirectory = "src/data";
        agentDataFile = "agent_data.m";
        testbedDataFile = "testbed_data.m";
        miscDataFile = "misc_data.m";
        init();
    }

    private static BufferedWriter newBufferedWriter(String directory, String filename) throws IOException {
        return new BufferedWriter(new FileWriter(new File(directory, filename)));
    }

    public void init() {
        final File dataDirectoryFile = new File(dataDirectory);
        if (!dataDirectoryFile.isDirectory()) {
            if (!dataDirectoryFile.mkdir()) {
                System.err.println("Unable to create directory: " + dataDirectory);
            }
        } else {
            System.out.println(dataDirectoryFile.getAbsolutePath() + " exists.");
        }

        agentPoses = new Vector<>();
        testbedPoses = new Vector<>();
        timestamps = new Vector<>();
        for (int i = 0; i < 3; i++)
            timestamps.add(0.0);

        String src = "src/main/resources";
        for (String filename : new String[]{"plot_pose_error.m", "plot_pose_orientation_error.m", "plot_poses.m"}) {
            try {
                Files.copy(new File(src, filename), new File(dataDirectory, filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addAgentPose(Matrix m) {
        agentPoses.add(m.copy());
    }

    public void addTestbedPose(Matrix m) {
        testbedPoses.add(m.copy());
    }

    public void addTimestamp(double tstamp) {
        timestamps.add(tstamp);
    }

    public void saveData() {
        out.print("Saving data..");
        try (BufferedWriter agentData = newBufferedWriter(dataDirectory, agentDataFile);
             BufferedWriter testbedData = newBufferedWriter(dataDirectory, testbedDataFile);
             BufferedWriter miscData = newBufferedWriter(dataDirectory, miscDataFile)) {

            writePoseVectorToMatlabFile(agentData, agentPoses, "agent_poses");
            writePoseVectorToMatlabFile(testbedData, testbedPoses, "testbed_poses");
            writeArrayToMatlabFile(miscData, timestamps, "time");

            Vector<Double> poseDiffs = new Vector<>();
            Vector<Double> poseOrientDiffs = new Vector<>();
            for (int i = 0; i < agentPoses.size(); i++) {
                poseDiffs.add(dist(agentPoses.elementAt(i), testbedPoses.elementAt(i)));
                final double degrees = toDegrees(testbedPoses.elementAt(i).get(2, 0) - agentPoses.elementAt(i).get(2, 0));
                poseOrientDiffs.add(degrees);
            }
            writeArrayToMatlabFile(agentData, poseDiffs, "pose_error");
            writeArrayToMatlabFile(agentData, poseOrientDiffs, "pose_orientation_error");

            Vector<Double> objx = new Vector<>();
            Vector<Double> objy = new Vector<>();
            for (SimObject simObject : values.getSimObjects()) {
                objx.add(simObject.x());
                objy.add(simObject.y());
            }

            writeArrayToMatlabFile(miscData, objx, "objects_loc_x");
            writeArrayToMatlabFile(miscData, objy, "objects_loc_y");
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("done.");
    }

    private void writePoseVectorToMatlabFile(BufferedWriter writer, Vector<Matrix> poses, String var) throws IOException {
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

    private void writeArrayToMatlabFile(BufferedWriter writer, Vector<Double> array, String var) throws IOException {
        writer.write("\n%begin auto output of array " + var + " (length: " + array.size() + ")\n" + var + " = [\n");
        for (int i = 0; i < array.size(); i++) {
            writer.write(" " + array.elementAt(i));
        }
        writer.write("\n]; %end auto output of array " + var);
    }
}
