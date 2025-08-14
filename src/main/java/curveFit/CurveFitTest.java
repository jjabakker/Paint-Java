package curveFit;

import paint.calculations.ExpDecayFitter;

class ExpDecayTest {

    public static void main(String[] args) {
        // Example data (synthetic exponential decay with noise)
        double[] x = new double[] {0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};
        double[] y = new double[] {2000, 1200, 750, 500, 300, 200, 150, 100, 70, 50};

        // Call the fitting function#if
        ExpDecayFitter.FitResult result = ExpDecayFitter.fit(x, y);

        // Print results
        System.out.printf("Tau (ms): %f%n", result.tauMs);
        System.out.printf("R²: %f%n", result.rSquared);
    }
}