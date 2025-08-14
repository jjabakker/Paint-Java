package paint.calculations;

import paint.objects.Track;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static paint.calculations.TauCalcResult.Status.TAU_INSUFFICIENT_POINTS;
import static paint.calculations.TauCalcResult.Status.TAU_SUCCESS;

public class Calculations {

    /**
     * Compute tau from a list of tracks.
     * Returns a TauCalcResult with status describing the outcome.
     */
    public static TauCalcResult calculateTau(List<Track> tracks,
                                             int minTracksForTau,
                                             double minRequireRSquared) {
        if (tracks == null || tracks.size() < minTracksForTau) {
            return new TauCalcResult(0.0, 0.0, TAU_INSUFFICIENT_POINTS);
        }

        // Create a frequency distribution of track durations
        double[] trackDurations = new double[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            trackDurations[i] = tracks.get(i).getTrackDuration();
        }
        Map<Double, Integer> frequencyDistribution = createFrequencyDistribution(trackDurations);


        // Unpack it into two double arrays.
        int numberItems = frequencyDistribution.size();
        double[] x = new double[numberItems];
        double[] y = new double[numberItems];
        int index = 0;
        for (Map.Entry<Double, Integer> entry : frequencyDistribution.entrySet()) {
            x[index] = entry.getKey();
            y[index] = entry.getValue();
            index++;
        }

        // Then do the fit
        ExpDecayFitter.FitResult result = ExpDecayFitter.fit(x, y);
        System.out.println(result.tauMs);
        System.out.println(result.rSquared);


        return new TauCalcResult(0.0, 0.0, TAU_SUCCESS);
    }


    /**
     * Build a frequency distribution (histogram) of exact durations.
     * Keys are the duration values; values are counts.
     */
    public static Map<Double, Integer> createFrequencyDistribution(double[] trackDurations) {
        Map<Double, Integer> frequencyDistribution = new TreeMap<>();
        if (trackDurations == null) return frequencyDistribution;

        for (double duration : trackDurations) {
            Integer prev = frequencyDistribution.get(duration);
            frequencyDistribution.put(duration, (prev == null ? 1 : prev + 1));
            // If you're sure you're on Java 8+, you can use:
            // frequencyDistribution.put(duration, frequencyDistribution.getOrDefault(duration, 0) + 1);
        }
        return frequencyDistribution;
    }
}