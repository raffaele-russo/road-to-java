import java.util.*;
import java.util.function.Supplier;

public class Exercise {
    static <T> T retry(int attempts, Supplier<T> operation) {
        throw new UnsupportedOperationException("TODO: bounded attempts; rethrow the last failure");
    }
    static boolean recordOnce(Set<UUID> processed, UUID eventId) {
        throw new UnsupportedOperationException("TODO: use the set's atomic contract in this single-threaded model");
    }
    public static void main(String[] args) {
        int[] calls = {0};
        assert retry(3, () -> { if (++calls[0] < 2) throw new RuntimeException(); return 42; }) == 42;
        var seen = new HashSet<UUID>(); var id = new UUID(0, 1);
        assert recordOnce(seen, id); assert !recordOnce(seen, id);
    }
}
