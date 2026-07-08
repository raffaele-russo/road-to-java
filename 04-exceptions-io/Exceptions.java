import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Checked vs unchecked, multi-catch, finally, try-with-resources (RAII substitute),
 * exception chaining, and modern NIO.2 file I/O.
 * Run:  java 04-exceptions-io/Exceptions.java
 */
public class Exceptions {

    public static void main(String[] args) throws IOException {
        uncheckedForBugs();
        checkedAndChaining();
        tryWithResources();
        fileIo();
    }

    static void uncheckedForBugs() {
        System.out.println("== unchecked (programming errors) ==");
        try {
            validateAge(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("  caught: " + e.getMessage());
        }
    }

    static void validateAge(int age) {
        if (age < 0) throw new IllegalArgumentException("age must be >= 0, got " + age);
    }

    // Custom checked exception forces callers to handle it.
    static class DataException extends Exception {
        DataException(String msg, Throwable cause) { super(msg, cause); }
    }

    static void checkedAndChaining() {
        System.out.println("\n== checked + cause chaining ==");
        try {
            loadData();
        } catch (DataException e) {
            System.out.println("  wrapper : " + e.getMessage());
            System.out.println("  cause   : " + e.getCause()); // original preserved
        }
    }

    static void loadData() throws DataException {
        try {
            Integer.parseInt("not-a-number");        // throws NumberFormatException
        } catch (NumberFormatException e) {
            throw new DataException("failed to load data", e); // wrap, keep cause
        }
    }

    // AutoCloseable resources are closed in reverse order, even on exception.
    static class Resource implements AutoCloseable {
        final String name;
        Resource(String name) { this.name = name; System.out.println("  open  " + name); }
        public void close() { System.out.println("  close " + name); }
    }

    static void tryWithResources() {
        System.out.println("\n== try-with-resources (RAII substitute) ==");
        try (var a = new Resource("A"); var b = new Resource("B")) {
            System.out.println("  using A and B");
        } // prints: close B, then close A
    }

    static void fileIo() throws IOException {
        System.out.println("\n== NIO.2 file I/O ==");
        Path tmp = Files.createTempFile("road-to-java", ".txt");
        Files.writeString(tmp, "line1\nline2\nline3\n");
        List<String> lines = Files.readAllLines(tmp);
        System.out.println("  wrote/read " + lines.size() + " lines: " + lines);
        Files.deleteIfExists(tmp);
        System.out.println("  cleaned up temp file");
    }
}
