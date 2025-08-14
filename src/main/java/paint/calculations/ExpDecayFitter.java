package paint.calculations;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import java.util.Collection;

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

        public MonoExpFitter(double[] initialGuess) {
            this.initialGuess = initialGuess;
        }

        @Override
        protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {
            AbstractCurveFitter.TheoreticalValuesFunction model =
                    new TheoreticalValuesFunction(new MonoExp(), points);

            return new LeastSquaresBuilder()
                    .start(initialGuess)
                    .model(model.getModelFunction(), model.getModelFunctionJacobian())
                    .target(points.stream().mapToDouble(WeightedObservedPoint::getY).toArray())
                    .maxEvaluations(1000)
                    .maxIterations(1000)
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
        if (x.length != y.length || x.length < 3) {
            throw new IllegalArgumentException("arrays must be same length and contain at least 3 points");
        }

        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < x.length; i++) {
            obs.add(x[i], y[i]);
        }

        double[] initialGuess = {2000, 4, 10};
        double[] params;

        try {
            MonoExpFitter fitter = new MonoExpFitter(initialGuess);
            params = fitter.fit(obs.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new FitResult(-2, 0);  // indicates fitting failure
        }

        double m = params[0];
        double t = params[1];
        double b = params[2];
        double tauMs = 1000.0 / t;

        // Compute R²
        double meanY = 0.0;
        for (double v : y) meanY += v;
        meanY /= y.length;

        double ssTot = 0.0;
        double ssRes = 0.0;
        for (int i = 0; i < x.length; i++) {
            double predicted = m * Math.exp(-t * x[i]) + b;
            ssRes += Math.pow(y[i] - predicted, 2);
            ssTot += Math.pow(y[i] - meanY, 2);
        }

        double rSquared = ssTot == 0 ? 0 : 1 - (ssRes / ssTot);
        return new FitResult(tauMs, rSquared);
    }
}
