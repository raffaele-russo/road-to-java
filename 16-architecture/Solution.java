import java.util.List;

public class Solution {
    static List<String> immutableSnapshot(List<String> values) { return List.copyOf(values); }

    static String transition(String current, String command) {
        if (current.equals("PENDING") && command.equals("CONFIRM")) return "CONFIRMED";
        throw new IllegalStateException("invalid transition: " + current + " / " + command);
    }

    public static void main(String[] args) {
        var source = new java.util.ArrayList<>(List.of("one"));
        var snapshot = immutableSnapshot(source);
        source.add("two");
        assert snapshot.equals(List.of("one"));
        assert transition("PENDING", "CONFIRM").equals("CONFIRMED");
        try { transition("CONFIRMED", "CONFIRM"); assert false; } catch (IllegalStateException expected) { }
        System.out.println("architecture solution passed");
    }
}
