
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static paint.utilities.Miscellaneous.formatDuration;

public class DurationFormatterTest {

    @Test
    void formatsZeroSeconds() {
        assertEquals("0 seconds", formatDuration(Duration.ofSeconds(0)));
    }

    @Test
    void formatsOnlySecondsSingular() {
        assertEquals("1 second", formatDuration(Duration.ofSeconds(1)));
    }

    @Test
    void formatsOnlySecondsPlural() {
        assertEquals("59 seconds", formatDuration(Duration.ofSeconds(59)));
    }

    @Test
    void formatsOnlyMinutesSingular() {
        assertEquals("1 minute", formatDuration(Duration.ofSeconds(60)));
    }

    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("1 minute 1 second", formatDuration(Duration.ofSeconds(61)));
        assertEquals("2 minutes 5 seconds", formatDuration(Duration.ofSeconds(125)));
    }

    @Test
    void formatsOnlyHoursSingular() {
        assertEquals("1 hour", formatDuration(Duration.ofSeconds(3600)));
    }

    @Test
    void formatsOnlyHoursPlural() {
        assertEquals("2 hours", formatDuration(Duration.ofSeconds(7200)));
    }

    @Test
    void formatsHoursMinutesSeconds() {
        assertEquals("1 hour 1 minute 1 second", formatDuration(Duration.ofSeconds(3661)));
        assertEquals("3 hours 4 minutes 5 seconds", formatDuration(Duration.ofSeconds(3 * 3600 + 4 * 60 + 5)));
    }

    @Test
    void omitsZeroComponents() {
        // No seconds shown when minutes > 0 and seconds == 0
        assertEquals("5 minutes", formatDuration(Duration.ofSeconds(5 * 60)));
        // No minutes shown when hours > 0 and minutes == 0
        assertEquals("7 hours 8 seconds", formatDuration(Duration.ofSeconds(7 * 3600 + 8)));
        // No minutes or seconds when both are zero
        assertEquals("9 hours", formatDuration(Duration.ofSeconds(9 * 3600)));
    }
}
