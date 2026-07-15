import java.util.*;

public class Exercise {
    record Row(UUID id, long version) { }

    static Row optimisticUpdate(Row stored, long expectedVersion) {
        throw new UnsupportedOperationException("TODO: compare and increment version");
    }

    static List<UUID> stablePage(List<UUID> ids, UUID after, int limit) {
        throw new UnsupportedOperationException("TODO: unique ordering and keyset boundary");
    }

    public static void main(String[] args) {
        var row = optimisticUpdate(new Row(new UUID(0, 1), 7), 7);
        assert row.version() == 8;
        try { optimisticUpdate(row, 7); assert false; } catch (IllegalStateException expected) { }
        assert stablePage(List.of(new UUID(0, 3), new UUID(0, 1), new UUID(0, 2)),
                new UUID(0, 1), 1).equals(List.of(new UUID(0, 2)));
    }
}
