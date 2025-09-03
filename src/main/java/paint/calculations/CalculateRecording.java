package paint.calculations;

import paint.objects.Recording;
import paint.objects.Square;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculateRecording {

    public static double calculateAverageTrackCountOfBackground(Recording recording, int nrOfAverageCountSquares) {

        List<Integer> trackCounts = new ArrayList<>();
        List<Square> squares = recording.getSquares();

        for (Square sq : squares) {
            trackCounts.add(sq.getTracks().size());
        }

        // Sort descending
        trackCounts.sort(Collections.reverseOrder());

        int total = 0;
        int n = 0;

        // Find the first non-zero value
        int m;
        for (m = trackCounts.size() - 1; m >= 0; m--) {
            if (trackCounts.get(m) != 0) {
                break;
            }
        }

        // Iterate from the smallest to the largest (like Python's reverse loop)
        for (int i = m; i >= 0; i--) {
            int v = trackCounts.get(i);

            total += v;
            n++;
            if (n >= nrOfAverageCountSquares) {
                break;
            }

        }

        if (n == 0) {
            return 0.0;
        } else {
            return (double) total / n;
        }
    }
}
