package paintUtilities;

public final class ExceptionUtils {
    private ExceptionUtils() {}

    // Message from the given throwable (trimmed to text after the last ':', if any)
    public static String friendlyMessage(Throwable t) {
        if (t == null)
            return "";
        String m = t.toString();
        int colon = m.lastIndexOf(':');
        return (colon != -1) ? m.substring(colon + 1).trim() : m;
    }

    // Same idea, but from the root cause
    public static String rootCauseFriendlyMessage(Throwable t) {
        if (t == null) return "";
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return friendlyMessage(cur);
    }
}