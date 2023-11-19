// Mark McKelvy
// CMPS 523
// Final Project
// File: Utils.java
package agv.sim.cmps523.math;

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;

import Jama.Matrix;
import java.util.Random;

public class MathUtil {
    private static final Random RANDOM;

    static {
        long randomSeed = 271828182;
        RANDOM = new Random(randomSeed);
    }

    // Squaring a number
    public static double square(double val) {
        return val * val;
    }

    // Just so that the code is more readable from the book algorithms.
    public static double prob(double a, double b) {
        return probTriangularDistribution(a, b);
    }

    // Page 123
    public static double probNormalDistribution(double a, double b) {
        return (1.0 / sqrt(2 * Math.PI * b * b)) * exp(-0.5 * (a * a) / (b * b));
    }

    // Page 123
    public static double probTriangularDistribution(double a, double b) {
        return Math.max(0, 1.0 / (sqrt(6) * b) - Math.abs(a) / (6 * b * b));
    }

    // Gaussian distributed value
    public static double gaussian(double mu, double sigma) {
        return sigma * RANDOM.nextGaussian() + mu;
    }

    // Real gaussian function
    public static double N(double mu, double sigmaSqr) {
        return gaussian(mu, sqrt(sigmaSqr));
    }

    // random number from minVal to maxVal inclusive
    public static double rand(double minVal, double maxVal) {
        return minVal + RANDOM.nextDouble() * (maxVal - minVal);
    }

    public static double clampAngle(double angle) {
        while (angle > Math.PI * 2 || angle < -Math.PI * 2) {
            if (angle > Math.PI * 2)
                angle -= Math.PI * 2;
            else
                angle += Math.PI * 2;
        }
        return angle;
    }

    public static double clampAngleWithinPiOverTwo(double angle) {
        if (angle > Math.PI / 2) {
            while (angle > Math.PI / 2) {
                angle -= Math.PI * 2;
            }
        } else if (angle < -Math.PI / 2) {
            while (angle < -Math.PI / 2) {
                angle += Math.PI * 2;
            }
        }
        return angle;
    }

    public static double dist(Matrix pose1, Matrix pose2) {
        return sqrt(square((pose1.get(0, 0) - pose2.get(0, 0))) +
                square((pose1.get(1, 0) - pose2.get(1, 0))));
    }
}
