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
    private String dataDirectory;
    private String agentDataFile;
    private String testbedDataFile;
    private String miscDataFile;

    private Vector<Matrix> agentPoses;
    private Vector<Matrix> testbedPoses;
    private Vector<Double> time;

    Logger() {
        this.init();
    }

    static BufferedWriter newBufferedWriter(String directory, String filename) throws IOException {
        return new BufferedWriter(new FileWriter(new File(directory, filename)));
    }

    static void copyFileToDirectory(String file, String sdir, String ddir) {
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
        setDataDirectory("src/data");
        setAgentDataFile("agent_data.m");
        setTestbedDataFile("testbed_data.m");
        setMiscDataFile("misc_data.m");


        final File dataDirectoryFile = new File(getDataDirectory());
        if (!dataDirectoryFile.isDirectory()) {
            if (!dataDirectoryFile.mkdir()) {
                System.err.println("Unable to create directory: " + getDataDirectory());
            }
        } else {
            System.out.println(dataDirectoryFile.getAbsolutePath() + " exists.");
        }

        setAgentPoses(new Vector<>());
        setTestbedPoses(new Vector<>());
        setTime(new Vector<>());
        for (int i = 0; i < 3; i++)
            getTime().add(0.0);

        String src = "src/main/resources";
        Logger.copyFileToDirectory("plot_pose_error.m", src, getDataDirectory());
        Logger.copyFileToDirectory("plot_pose_orientation_error.m", src, getDataDirectory());
        Logger.copyFileToDirectory("plot_poses.m", src, getDataDirectory());
    }

    void saveAgentPose(Matrix m) {
        getAgentPoses().add(m.copy());
    }

    void saveTestbedPose(Matrix m) {
        getTestbedPoses().add(m.copy());
    }

    void saveTime(double tstamp) {
        getTime().add(tstamp);
    }

    void saveData() {
        out.print("Saving data..");
        try {
            BufferedWriter agent_data = Logger.newBufferedWriter(getDataDirectory(), getAgentDataFile());
            BufferedWriter testbed_data = Logger.newBufferedWriter(getDataDirectory(), getTestbedDataFile());
            BufferedWriter misc_data = Logger.newBufferedWriter(getDataDirectory(), getMiscDataFile());

            this.writePoseVectorToMatlabFile(agent_data, getAgentPoses(), "agent_poses");
            this.writePoseVectorToMatlabFile(testbed_data, getTestbedPoses(), "testbed_poses");
            this.writeArrayToMatlabFile(misc_data, getTime(), "time");

            Vector<Double> pose_diffs = new Vector<>();
            Vector<Double> pose_orient_diffs = new Vector<>();
            for (int i = 0; i < getAgentPoses().size(); i++) {
                pose_diffs.add(Utils.dist(getAgentPoses().elementAt(i), getTestbedPoses().elementAt(i)));
                final double degrees = Math.toDegrees(getTestbedPoses().elementAt(i).get(2, 0) - getAgentPoses().elementAt(i).get(2, 0));
                pose_orient_diffs.add(degrees);
            }
            this.writeArrayToMatlabFile(agent_data, pose_diffs, "pose_error");
            this.writeArrayToMatlabFile(agent_data, pose_orient_diffs, "pose_orientation_error");

            Vector<Double> objx = new Vector<>();
            Vector<Double> objy = new Vector<>();
            for (int i = 0; i < AGVsim.getTestbed().numObjects(); i++) {
                objx.add(AGVsim.getTestbed().objectAt(i).x());
                objy.add(AGVsim.getTestbed().objectAt(i).y());
            }

            this.writeArrayToMatlabFile(misc_data, objx, "objects_loc_x");
            this.writeArrayToMatlabFile(misc_data, objy, "objects_loc_y");

            agent_data.close();
            testbed_data.close();
            misc_data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("done.");
    }

    void writePoseVectorToMatlabFile(BufferedWriter writer, Vector<Matrix> poses, String var) throws IOException {
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

    void writeArrayToMatlabFile(BufferedWriter writer, double[] array, String var) throws IOException {
        writer.write("\n%begin auto output of array " + var + " (length: " + array.length + ")\n" + var + " = [\n");
        for (final double v : array) {
            writer.write(" " + v);
        }
        writer.write("\n]; %end auto output of array " + var);
    }

    void writeArrayToMatlabFile(BufferedWriter writer, Vector<Double> array, String var) throws IOException {
        writer.write("\n%begin auto output of array " + var + " (length: " + array.size() + ")\n" + var + " = [\n");
        for (int i = 0; i < array.size(); i++) {
            writer.write(" " + array.elementAt(i));
        }
        writer.write("\n]; %end auto output of array " + var);
    }

    void writeDoubleArrayToMatlabFile(BufferedWriter writer, double[] array1, double[] array2, String var) throws IOException {
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

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getAgentDataFile() {
        return agentDataFile;
    }

    public void setAgentDataFile(String agentDataFile) {
        this.agentDataFile = agentDataFile;
    }

    public String getTestbedDataFile() {
        return testbedDataFile;
    }

    public void setTestbedDataFile(String testbedDataFile) {
        this.testbedDataFile = testbedDataFile;
    }

    public String getMiscDataFile() {
        return miscDataFile;
    }

    public void setMiscDataFile(String miscDataFile) {
        this.miscDataFile = miscDataFile;
    }

    public Vector<Matrix> getAgentPoses() {
        return agentPoses;
    }

    public void setAgentPoses(Vector<Matrix> agentPoses) {
        this.agentPoses = agentPoses;
    }

    public Vector<Matrix> getTestbedPoses() {
        return testbedPoses;
    }

    public void setTestbedPoses(Vector<Matrix> testbedPoses) {
        this.testbedPoses = testbedPoses;
    }

    public Vector<Double> getTime() {
        return time;
    }

    public void setTime(Vector<Double> time) {
        this.time = time;
    }
}
