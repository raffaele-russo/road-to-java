public class Exercise {
    static String firstEvidence(String symptom) {
        throw new UnsupportedOperationException("TODO: map symptom to evidence, not a guessed fix");
    }

    static boolean shouldBeReady(boolean databaseReachable, boolean processHealthy) {
        throw new UnsupportedOperationException("TODO: readiness includes required dependencies");
    }

    public static void main(String[] args) {
        assert firstEvidence("high-cpu").equals("thread-cpu+jfr");
        assert firstEvidence("memory-growth").equals("heap-after-gc");
        assert !shouldBeReady(false, true);
        assert shouldBeReady(true, true);
    }
}
