package paint.calculations;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExpDecayFitter {

    // The exponential function model: y = m * exp(-t * x) + b
    static class MonoExp implements ParametricUnivariateFunction {
        @Override
        public double value(double x, double[] parameters) {
            double m = parameters[0];
            double t = parameters[1];
            double b = parameters[2];
            return m * Math.exp(-t * x) + b;
        }

        @Override
        public double[] gradient(double x, double[] parameters) {
            double m = parameters[0];
            double t = parameters[1];
            double expTerm = Math.exp(-t * x);
            return new double[] {
                    expTerm,             // ∂y/∂m
                    -m * x * expTerm,    // ∂y/∂t
                    1.0                  // ∂y/∂b
            };
        }
    }

    // Custom fitter using non-deprecated APIs
    static class MonoExpFitter extends AbstractCurveFitter {
        private final double[] initialGuess;
        private final int maxEval;
        private final int maxIter;

        public MonoExpFitter(double[] initialGuess, int maxEval, int maxIter) {
            this.initialGuess = initialGuess;
            this.maxEval = maxEval;
            this.maxIter = maxIter;
        }

        @Override
        protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {
            AbstractCurveFitter.TheoreticalValuesFunction model =
                    new TheoreticalValuesFunction(new MonoExp(), points);

            return new LeastSquaresBuilder()
                    .start(initialGuess)
                    .model(model.getModelFunction(), model.getModelFunctionJacobian())
                    .target(points.stream().mapToDouble(WeightedObservedPoint::getY).toArray())
                    .maxEvaluations(maxEval)
                    .maxIterations(maxIter)
                    .build();
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

    public static FitResult fit(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length || x.length < 3) {
            throw new IllegalArgumentException("arrays must be same length and contain at least 3 points");
        }

        // Filter out any non-finite data and collect observed points
        List<WeightedObservedPoint> pts = new ArrayList<>(x.length);
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < x.length; i++) {
            double xi = x[i];
            double yi = y[i];
            if (Double.isFinite(xi) && Double.isFinite(yi)) {
                pts.add(new WeightedObservedPoint(1.0, xi, yi));
                if (yi < minY) minY = yi;
                if (yi > maxY) maxY = yi;
                if (xi > maxX) maxX = xi;
            }
        }

        if (pts.size() < 3) {
            return new FitResult(Double.NaN, 0.0);
        }

        // Degenerate case: flat or nearly flat data
        if (maxY - minY <= 1e-9) {
            return new FitResult(Double.NaN, 0.0);
        }

        // Build a data-driven initial guess
        double[] initialGuess = buildInitialGuess(pts, minY, maxY, maxX);

        double[] params;
        try {
            // Give the optimizer more room now that our initial guess is sensible
            MonoExpFitter fitter = new MonoExpFitter(initialGuess, 10000, 10000);
            params = fitter.fit(pts);
        } catch (Exception e) {
            // Fitting failed to converge
            return new FitResult(Double.NaN, 0.0);
        }

        double m = params[0];
        double t = params[1];
        double b = params[2];

        if (!Double.isFinite(m) || !Double.isFinite(t) || !Double.isFinite(b) || t <= 0) {
            return new FitResult(Double.NaN, 0.0);
        }

        double tauMs = 1000.0 / t;

        // Compute R²
        double meanY = 0.0;
        for (double v : y) meanY += v;
        meanY /= y.length;

        double ssTot = 0.0;
        double ssRes = 0.0;
        for (int i = 0; i < x.length; i++) {
            if (!Double.isFinite(x[i]) || !Double.isFinite(y[i])) continue;
            double predicted = m * Math.exp(-t * x[i]) + b;
            ssRes += Math.pow(y[i] - predicted, 2);
            ssTot += Math.pow(y[i] - meanY, 2);
        }

        double rSquared = ssTot == 0 ? 0 : 1 - (ssRes / ssTot);
        return new FitResult(tauMs, rSquared);
    }

    private static double[] buildInitialGuess(List<WeightedObservedPoint> pts, double minY, double maxY, double maxX) {
        // Baseline guess: smallest y value (counts at the tail)
        double b = Math.max(0.0, minY);

        // Amplitude guess
        double m = Math.max(1e-6, maxY - b);

        // Estimate t by linearizing: ln(y - b) = ln(m) - t * x
        // Use only points where y > b + epsilon
        double eps = Math.max(1e-6, 0.01 * m);
        double sumX = 0, sumXX = 0, sumY = 0, sumXY = 0;
        int n = 0;
        for (WeightedObservedPoint p : pts) {
            double yiAdj = p.getY() - b;
            if (yiAdj > eps) {
                double lx = p.getX();
                double ly = Math.log(yiAdj);
                sumX += lx;
                sumXX += lx * lx;
                sumY += ly;
                sumXY += lx * ly;
                n++;
            }
        }

        double t;
        if (n >= 2) {
            double denom = n * sumXX - sumX * sumX;
            if (Math.abs(denom) < 1e-12) {
                t = 1.0 / Math.max(1.0, maxX); // fallback
            } else {
                double slope = (n * sumXY - sumX * sumY) / denom; // slope of ly vs x
                t = Math.max(1e-9, -slope); // t should be positive
            }
        } else {
            // Fallback: a small positive t related to the x-range
            t = 1.0 / Math.max(1.0, maxX);
        }

        // Clamp to reasonable ranges to help the optimizer
        m = clamp(m, 1e-9, 1e9);
        t = clamp(t, 1e-9, 1e3);
        b = clamp(b, 0.0, Math.max(1.0, maxY));

        return new double[] { m, t, b };
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
