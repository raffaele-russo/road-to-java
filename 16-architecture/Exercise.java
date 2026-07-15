import java.util.List;

public class Exercise {
    static List<String> immutableSnapshot(List<String> values) {
        throw new UnsupportedOperationException("TODO: break aliases and reject later mutation");
    }

    static String transition(String current, String command) {
        throw new UnsupportedOperationException("TODO: permit confirm only from PENDING");
    }

    public static void main(String[] args) {
        var source = new java.util.ArrayList<>(List.of("one"));
        var snapshot = immutableSnapshot(source);
        source.add("two");
        assert snapshot.equals(List.of("one"));
        assert transition("PENDING", "CONFIRM").equals("CONFIRMED");
        try { transition("CONFIRMED", "CONFIRM"); assert false; } catch (IllegalStateException expected) { }
    }
}
