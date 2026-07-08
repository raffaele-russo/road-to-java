import java.util.List;

// Practice: build a small bank-transfer validator. Run once you think you're done:
//   java -ea 04-exceptions-io/Exercise.java
public class Exercise {

    /** TODO: unchecked exception. Constructor takes the shortfall amount; build a
     *  message like "short by 50.0" and expose shortfall() for callers to inspect. */
    static class InsufficientFundsException extends RuntimeException {
        // TODO: field + constructor + shortfall() accessor
    }

    /** TODO: unchecked exception, simple message-only constructor. */
    static class InvalidAmountException extends RuntimeException {
        // TODO: constructor(String message)
    }

    /** TODO: amount <= 0 -> InvalidAmountException.
     *  amount > balance -> InsufficientFundsException with shortfall = amount - balance.
     *  otherwise -> return balance - amount. */
    static double withdraw(double balance, double amount) {
        throw new UnsupportedOperationException("TODO");
    }

    /** TODO: parse every line as a double. If ALL succeed, return the list of parsed
     *  values in order. If ANY fail, do NOT stop at the first failure — attempt every
     *  line, collect a NumberFormatException per bad line via addSuppressed() on one
     *  aggregate NumberFormatException, then throw that aggregate exception. The
     *  aggregate's message should be "invalid amounts: " followed by the bad lines,
     *  comma-separated, in the order they appeared. */
    static List<Double> parseAmounts(List<String> lines) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        assert withdraw(100, 30) == 70.0;

        try {
            withdraw(100, 150);
            throw new AssertionError("expected InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            assert e.shortfall() == 50.0 : "shortfall should be 50.0, got " + e.shortfall();
        }

        try {
            withdraw(100, -5);
            throw new AssertionError("expected InvalidAmountException");
        } catch (InvalidAmountException expected) { /* ok */ }

        assert parseAmounts(List.of("1.5", "2.5", "3")).equals(List.of(1.5, 2.5, 3.0));

        try {
            parseAmounts(List.of("1.5", "oops", "3", "nope"));
            throw new AssertionError("expected NumberFormatException");
        } catch (NumberFormatException e) {
            assert e.getMessage().equals("invalid amounts: oops, nope") : "got: " + e.getMessage();
            assert e.getSuppressed().length == 2 : "should have one suppressed exception per bad line";
        }

        System.out.println("All good — module 04 exercise complete.");
    }
}
