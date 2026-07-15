import java.util.*;

public class Solution {
    record Row(UUID id, long version) { }
    static Row optimisticUpdate(Row stored, long expectedVersion) {
        if (stored.version() != expectedVersion) throw new IllegalStateException("stale version");
        return new Row(stored.id(), stored.version() + 1);
    }
    static List<UUID> stablePage(List<UUID> ids, UUID after, int limit) {
        if (limit < 1) throw new IllegalArgumentException("limit");
        return ids.stream().sorted().filter(id -> after == null || id.compareTo(after) > 0).limit(limit).toList();
    }
    public static void main(String[] args) {
        var row = optimisticUpdate(new Row(new UUID(0, 1), 7), 7);
        assert row.version() == 8;
        try { optimisticUpdate(row, 7); assert false; } catch (IllegalStateException expected) { }
        assert stablePage(List.of(new UUID(0, 3), new UUID(0, 1), new UUID(0, 2)), new UUID(0, 1), 1)
                .equals(List.of(new UUID(0, 2)));
        System.out.println("persistence solution passed");
    }
}
