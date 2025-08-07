package CurveFit;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.CurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;


public class ExpDecayFitter {

    // Define the model function: m * exp(-t * x) + b
    static class MonoExp implements ParametricUnivariateFunction {
        public double value(double x, double[] parameters) {
            double m = parameters[0];
            double t = parameters[1];
            double b = parameters[2];
            return m * Math.exp(-t * x) + b;
        }

        public double[] gradient(double x, double[] parameters) {
            double m = parameters[0];
            double t = parameters[1];
            // Gradient w.r.t parameters m, t, b
            double expTerm = Math.exp(-t * x);
            return new double[] {
                    expTerm,                // d/dm
                    -m * x * expTerm,       // d/dt
                    1.0                     // d/db
            };
        }
    }

    public static class FitResult {
        public final double tauMs;
        public final double rSquared;

        public FitResult(double tauMs, double rSquared) {
            this.tauMs = tauMs;
            this.rSquared = rSquared;
        }
    }

    // Fit function: x and y are input data points
    public static FitResult fit(double[] x, double[] y) {
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < x.length; i++) {
            obs.add(x[i], y[i]);
        }

        CurveFitter<ParametricUnivariateFunction> fitter = new CurveFitter<>(new LevenbergMarquardtOptimizer());


        for (WeightedObservedPoint point : obs.toList()) {
            fitter.addObservedPoint(point);
        }

        double[] initialGuess = {2000, 4, 10};  // similar to p0 in Python

        double[] params;
        try {
            params = fitter.fit(new MonoExp(), initialGuess);
        } catch (Exception e) {
            e.printStackTrace();
            return new FitResult(-2, 0);  // indicate failure like in Python code
        }

        double m = params[0];
        double t = params[1];
        double b = params[2];

        double tauMs = 1000.0 / t;  // tau in milliseconds

        // Compute R squared
        double meanY = 0;
        for (double v : y) meanY += v;
        meanY /= y.length;

        double ssTot = 0;
        double ssRes = 0;
        for (int i = 0; i < x.length; i++) {
            double fitVal = m * Math.exp(-t * x[i]) + b;
            ssRes += Math.pow(y[i] - fitVal, 2);
            ssTot += Math.pow(y[i] - meanY, 2);
        }

        double rSquared = ssTot == 0 ? 0 : 1 - ssRes / ssTot;

        return new FitResult(tauMs, rSquared);
    }
}
