import java.util.ArrayList;
import java.util.List;

/** Reference solution for Exercise.java. Run with: java -ea 04-exceptions-io/Solution.java */
public class Solution {
    static class InsufficientFundsException extends RuntimeException {
        private final double shortfall;
        InsufficientFundsException(double shortfall) {
            super("short by " + shortfall);
            this.shortfall = shortfall;
        }
        double shortfall() { return shortfall; }
    }

    static class InvalidAmountException extends RuntimeException {
        InvalidAmountException(String message) { super(message); }
    }

    static double withdraw(double balance, double amount) {
        if (amount <= 0) throw new InvalidAmountException("amount must be positive");
        if (amount > balance) throw new InsufficientFundsException(amount - balance);
        return balance - amount;
    }

    static List<Double> parseAmounts(List<String> lines) {
        List<Double> parsed = new ArrayList<>();
        List<String> bad = new ArrayList<>();
        List<NumberFormatException> failures = new ArrayList<>();
        for (String line : lines) {
            try {
                parsed.add(Double.parseDouble(line));
            } catch (NumberFormatException failure) {
                bad.add(line);
                failures.add(failure);
            }
        }
        if (!failures.isEmpty()) {
            NumberFormatException aggregate =
                new NumberFormatException("invalid amounts: " + String.join(", ", bad));
            failures.forEach(aggregate::addSuppressed);
            throw aggregate;
        }
        return List.copyOf(parsed);
    }

    public static void main(String[] args) {
        assert withdraw(100, 30) == 70;
        try { parseAmounts(List.of("1", "bad", "also-bad")); }
        catch (NumberFormatException e) { assert e.getSuppressed().length == 2; }
        System.out.println("Module 04 reference solution passed.");
    }
}
