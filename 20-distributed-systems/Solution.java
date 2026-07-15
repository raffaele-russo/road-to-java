import java.util.*;
import java.util.function.Supplier;

public class Solution {
    static <T> T retry(int attempts, Supplier<T> operation) {
        if (attempts < 1) throw new IllegalArgumentException("attempts");
        RuntimeException last = null;
        for (int i = 0; i < attempts; i++) {
            try { return operation.get(); } catch (RuntimeException failure) { last = failure; }
        }
        throw last;
    }
    static boolean recordOnce(Set<UUID> processed, UUID eventId) { return processed.add(eventId); }
    public static void main(String[] args) {
        int[] calls = {0};
        assert retry(3, () -> { if (++calls[0] < 2) throw new RuntimeException(); return 42; }) == 42;
        var seen = new HashSet<UUID>(); var id = new UUID(0, 1);
        assert recordOnce(seen, id); assert !recordOnce(seen, id);
        System.out.println("reliability solution passed");
    }
}
