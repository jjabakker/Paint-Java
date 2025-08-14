package paint.calculations;

import paint.objects.Track;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        CalculateTauExpDecayFitter.FitResult fr = CalculateTauExpDecayFitter.fit(x, y);
        // 1) Reject non-finite results
        if (!Double.isFinite(fr.rSquared) || !Double.isFinite(fr.tauMs)) {
            return new CalculateTauResult(fr.tauMs, fr.rSquared, CalculateTauResult.Status.TAU_NO_FIT);
        }

        // 2) Apply threshold
        if (fr.rSquared < minRequiredRSquared) {
            return new CalculateTauResult(fr.tauMs, fr.rSquared, CalculateTauResult.Status.TAU_RSQUARED_TOO_LOW);
        }

        return new CalculateTauResult(fr.tauMs, fr.rSquared, CalculateTauResult.Status.TAU_SUCCESS);
    }

    /** Build frequency distribution: key = duration, value = count. */
    public static Map<Double, Integer> createFrequencyDistribution(double[] trackDurations) {
        Map<Double, Integer> frequencyDistribution = new TreeMap<>();
        if (trackDurations == null) return frequencyDistribution;

        for (double duration : trackDurations) {
            Integer prev = frequencyDistribution.get(duration);
            frequencyDistribution.put(duration, (prev == null ? 1 : prev + 1));
            // (On Java 8 you can also use getOrDefault if preferred.)
            // frequencyDistribution.put(duration, frequencyDistribution.getOrDefault(duration, 0) + 1);
        }
        return frequencyDistribution;
    }
}