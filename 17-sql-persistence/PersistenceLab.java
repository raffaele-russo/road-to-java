import java.util.*;

/** In-memory model of database contracts; PostgreSQL proof remains in module 22. */
public class PersistenceLab {
    record Row(UUID id, long version, String status) { }

    static Row update(Row stored, long expectedVersion, String status) {
        if (stored.version() != expectedVersion) throw new IllegalStateException("stale version");
        return new Row(stored.id(), stored.version() + 1, status);
    }

    static List<Row> pageAfter(List<Row> rows, UUID after, int limit) {
        return rows.stream().sorted(Comparator.comparing(Row::id))
                .filter(row -> after == null || row.id().compareTo(after) > 0).limit(limit).toList();
    }

    public static void main(String[] args) {
        var id = new UUID(0, 2);
        var updated = update(new Row(id, 4, "PENDING"), 4, "CONFIRMED");
        assert updated.version() == 5;
        try { update(updated, 4, "CANCELLED"); assert false; } catch (IllegalStateException expected) { }
        var rows = List.of(new Row(new UUID(0, 3), 0, "PENDING"), new Row(id, 0, "PENDING"));
        assert pageAfter(rows, id, 10).getFirst().id().equals(new UUID(0, 3));
        System.out.println("optimistic locking and stable keyset pagination passed");
    }
}
