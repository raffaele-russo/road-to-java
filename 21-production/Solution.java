public class Solution {
    static String firstEvidence(String symptom) {
        return switch (symptom) {
            case "high-cpu" -> "thread-cpu+jfr";
            case "memory-growth" -> "heap-after-gc";
            case "stuck-requests" -> "traces+thread-dump";
            case "long-pauses" -> "gc+jfr";
            default -> throw new IllegalArgumentException("unknown symptom");
        };
    }

    static boolean shouldBeReady(boolean databaseReachable, boolean processHealthy) {
        return processHealthy && databaseReachable;
    }

    public static void main(String[] args) {
        assert firstEvidence("high-cpu").equals("thread-cpu+jfr");
        assert firstEvidence("memory-growth").equals("heap-after-gc");
        assert !shouldBeReady(false, true);
        assert shouldBeReady(true, true);
        System.out.println("production diagnosis solution passed");
    }
}
