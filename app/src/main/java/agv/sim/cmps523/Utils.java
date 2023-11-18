// Mark McKelvy
// CMPS 523
// Final Project
// File: Utils.java
package agv.sim.cmps523;

import Jama.Matrix;
import java.util.Random;

public class Utils {
    static long randSeed = 271828182;
    static Random randGen = new Random(randSeed);

    // Squaring a number
    static double square(double val) {
        return val * val;
    }

    // Just so that the code is more readable from the book algorithms.
    static double prob(double a, double b) {
        return Utils.prob_triangular_distribution(a, b);
    }

    // Page 123
    static double prob_normal_distribution(double a, double b) {
        return (1.0 / Math.sqrt(2 * Math.PI * b * b)) * Math.exp(-0.5 * (a * a) / (b * b));
    }

    // Page 123
    static double prob_triangular_distribution(double a, double b) {
        return Math.max(0, 1.0 / (Math.sqrt(6) * b) - Math.abs(a) / (6 * b * b));
    }

    // Gaussian distributed value
    static double gaussian(double mu, double sigma) {
        return sigma * randGen.nextGaussian() + mu;
    }

    // Real gaussian function
    static double N(double mu, double sigma_sqr) {
        return Utils.gaussian(mu, Math.sqrt(sigma_sqr));
    }

    // random number from min_val to max_val inclusive
    static double rand(double min_val, double max_val) {
        return min_val + Math.random() * (max_val - min_val);
    }

    static double clamp_angle(double angle) {
        while (angle > Math.PI * 2 || angle < -Math.PI * 2) {
            if (angle > Math.PI * 2)
                angle -= Math.PI * 2;
            else
                angle += Math.PI * 2;
        }
        return angle;
    }

    static double clamp_angle_within_pi_over_two(double angle) {
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

    static double dist(Matrix pose1, Matrix pose2) {
        return Math.sqrt(Utils.square((pose1.get(0, 0) - pose2.get(0, 0))) +
                Utils.square((pose1.get(1, 0) - pose2.get(1, 0))));
    }

    static double dist(Object pose1, Object pose2) {
        return Utils.dist((Matrix) pose1, (Matrix) pose2);
    }
}
