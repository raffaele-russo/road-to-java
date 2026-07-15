import java.util.*;
import java.util.function.Supplier;

public class ReliabilityLab {
    static <T> T retry(int attempts, Supplier<T> operation) {
        if (attempts < 1) throw new IllegalArgumentException("attempts");
        RuntimeException last = null;
        for (int i = 0; i < attempts; i++) {
            try { return operation.get(); } catch (RuntimeException failure) { last = failure; }
        }
        throw last;
    }

    static final class IdempotentEffects {
        private final Set<UUID> processed = new HashSet<>();
        private int effects;
        void accept(UUID eventId) { if (processed.add(eventId)) effects++; }
        int effects() { return effects; }
    }

    public static void main(String[] args) {
        int[] calls = {0};
        assert retry(3, () -> { if (++calls[0] < 3) throw new RuntimeException("transient"); return "ok"; }).equals("ok");
        var consumer = new IdempotentEffects();
        var id = new UUID(0, 1);
        consumer.accept(id); consumer.accept(id);
        assert consumer.effects() == 1;
        System.out.println("bounded retry and duplicate-safe effect passed");
    }
}
