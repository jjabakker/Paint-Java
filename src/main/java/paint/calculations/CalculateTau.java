package paint.calculations;

import org.apache.commons.math3.util.FastMath;
import paint.objects.Track;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import static paint.calculations.CalculateTauResult.Status.*;

public class CalculateTau {

    /**
     * Calculates tau by fitting a mono-exponential decay to a frequency
     * distribution of track durations. Returns a TauCalcResult with status.
     *
     * Flow:
     *   tracks -> durations[] -> TreeMap<Double, Integer> freq -> x[], y[] -> fit() -> TauCalcResult
     *
     * @param tracks               input tracks
     * @param minTracksForTau      minimum number of tracks required to attempt a fit
     * @param minRequiredRSquared  minimum acceptable R^2 for success
     */
    public static CalculateTauResult calculateTau(List<Track> tracks,
                                                  int minTracksForTau,
                                                  double minRequiredRSquared) {
        if (tracks == null || tracks.size() < minTracksForTau) {
            return new CalculateTauResult(0.0, 0.0, TAU_INSUFFICIENT_POINTS);
        }

        // 1) Extract durations
        final int n = tracks.size();
        double[] durations = new double[n];
        for (int i = 0; i < n; i++) {
            durations[i] = tracks.get(i).getTrackDuration();
        }

        // 2) Build frequency distribution (sorted by duration)
        Map<Double, Integer> freq = createFrequencyDistribution(durations);

        // Need at least 2 distinct x-values to fit
        if (freq.size() < 2) {
            return new CalculateTauResult(0.0, 0.0, TAU_NO_FIT);
        }

        // 3) Convert to arrays for fitting
        double[] x = new double[freq.size()];
        double[] y = new double[freq.size()];
        int k = 0;
        for (Map.Entry<Double, Integer> e : freq.entrySet()) {
            x[k] = e.getKey();
            y[k] = e.getValue();
            k++;
        }

        // 4) Fit and evaluate quality
        CalculateTauExpDecayFitterNew.FitResult fr = CalculateTauExpDecayFitterNew.fit(x, y);

        // 5) Reject non-finite results
        if (!Double.isFinite(fr.rSquared) || !Double.isFinite(fr.tauMs)) {
            return new CalculateTauResult(fr.tauMs, fr.rSquared, CalculateTauResult.Status.TAU_NO_FIT);
        }

        // 6) Apply threshold
        if (fr.rSquared < minRequiredRSquared) {
            return new CalculateTauResult(fr.tauMs, fr.rSquared, CalculateTauResult.Status.TAU_RSQUARED_TOO_LOW);
        }

        return new CalculateTauResult(fr.tauMs, fr.rSquared, CalculateTauResult.Status.TAU_SUCCESS);
    }

    /** Build frequency distribution: key = duration, value = count. */
    private static Map<Double, Integer> createFrequencyDistribution(double[] trackDurations) {
        Map<Double, Integer> frequencyDistribution = new TreeMap<>();
        if (trackDurations == null) return frequencyDistribution;

        for (double duration : trackDurations) {
            Integer prev = frequencyDistribution.get(duration);
            frequencyDistribution.put(duration, (prev == null ? 1 : prev + 1));
        }
        return frequencyDistribution;
    }
}




/**
 * Fits y = m * exp(-t * x) + b to (x, y) and returns tau (ms) and R^2.
 * Unified single-class version: no AbstractCurveFitter subclassing needed.
 */
class CalculateTauExpDecayFitterNew {

    private CalculateTauExpDecayFitterNew() {}

    /** Immutable result. */
    public static final class FitResult {
        public final double tauMs;
        public final double rSquared;
        public FitResult(double tauMs, double rSquared) {
            this.tauMs = tauMs;
            this.rSquared = rSquared;
        }
    }

    /**
     * Main entrypoint.
     * @param x domain values
     * @param y range values (e.g., counts)
     */
    public static FitResult fit(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length || x.length < 2) {
            return new FitResult(Double.NaN, Double.NaN);
        }

        // Build model + Jacobian: y = m * exp(-t * x) + b, params p = [m, t, b]
        final MultivariateJacobianFunction model = new MultivariateJacobianFunction() {
            @Override
            public org.apache.commons.math3.util.Pair<RealVector, RealMatrix> value(final RealVector p) {
                final double m = p.getEntry(0);
                final double t = p.getEntry(1);
                final double b = p.getEntry(2);

                final double[] values = new double[x.length];
                final double[][] jac = new double[x.length][3];

                for (int i = 0; i < x.length; i++) {
                    double e = FastMath.exp(-t * x[i]);
                    values[i] = m * e + b;
                    jac[i][0] = e;              // d/dm
                    jac[i][1] = -m * x[i] * e;  // d/dt
                    jac[i][2] = 1.0;            // d/db
                }

                return new org.apache.commons.math3.util.Pair<RealVector, RealMatrix>(
                        new ArrayRealVector(values, false),
                        new Array2DRowRealMatrix(jac, false)
                );
            }
        };

        // Initial guess [m, t, b]
        final double[] p0 = initialGuess(x, y);

        // Weights: identity (you can inject real weights here if desired)
        final double[] w = new double[y.length];
        Arrays.fill(w, 1.0);
        final RealMatrix weight = new DiagonalMatrix(w);

        // Build and solve least-squares
        LeastSquaresProblem problem = new LeastSquaresBuilder()
                .start(p0)
                .model(model)
                .target(y)           // observed y
                .weight(weight)      // diagonal weights
                .maxEvaluations(10_000)
                .maxIterations(1_000)
                .build();

        LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer();
        LeastSquaresOptimizer.Optimum optimum;
        try {
            optimum = optimizer.optimize(problem);
        } catch (Throwable t) {
            return new FitResult(Double.NaN, Double.NaN);
        }

        final double[] p = optimum.getPoint().toArray();
        final double m = p[0], t = p[1], b = p[2];

        // tau (ms) = 1000 / t  (guard t>0)
        final double tauMs = (t > 0.0) ? (1000.0 / t) : Double.NaN;

        // R^2 on original data
        final double r2 = computeRSquared(x, y, m, t, b);

        return new FitResult(tauMs, r2);
    }

    /** Heuristic initial guess for [m, t, b]. */
    private static double[] initialGuess(double[] x, double[] y) {
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < y.length; i++) {
            if (y[i] < minY) minY = y[i];
            if (y[i] > maxY) maxY = y[i];
            if (x[i] > maxX) maxX = x[i];
        }

        // Baseline guess b = min(y)
        double b = Math.max(0.0, minY);

        // Amplitude guess m = max(y) - b
        double m = Math.max(1e-6, maxY - b);

        // Rough t from a linearized tail: ln(y - b) ≈ ln(m) - t x
        double eps = Math.max(1e-6, 0.01 * m);
        double sumX = 0, sumXX = 0, sumY = 0, sumXY = 0;
        int n = 0;
        for (int i = 0; i < x.length; i++) {
            double yiAdj = y[i] - b;
            if (yiAdj > eps) {
                double lx = x[i];
                double ly = FastMath.log(yiAdj);
                sumX += lx;
                sumXX += lx * lx;
                sumY += ly;
                sumXY += lx * ly;
                n++;
            }
        }
        double t;
        if (n >= 2) {
            double denom = (n * sumXX - sumX * sumX);
            double slope = (denom == 0.0) ? -1.0 : (n * sumXY - sumX * sumY) / denom;
            t = Math.max(1e-9, -slope); // slope ≈ -t
        } else {
            t = 1.0 / Math.max(1e-3, maxX); // fallback
        }

        // Clamp to reasonable ranges
        m = clamp(m, 1e-9, 1e9);
        t = clamp(t, 1e-9, 1e3);
        b = clamp(b, 0.0, Math.max(1.0, maxY));

        return new double[] { m, t, b };
    }

    private static double computeRSquared(double[] x, double[] y, double m, double t, double b) {
        double meanY = 0.0;
        for (double v : y) meanY += v;
        meanY /= y.length;

        double ssRes = 0.0, ssTot = 0.0;
        for (int i = 0; i < y.length; i++) {
            double predicted = m * FastMath.exp(-t * x[i]) + b;
            double diff = y[i] - predicted;
            ssRes += diff * diff;
            double dy = y[i] - meanY;
            ssTot += dy * dy;
        }
        return (ssTot == 0.0) ? Double.NaN : 1.0 - (ssRes / ssTot);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}