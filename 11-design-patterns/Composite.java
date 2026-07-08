import java.util.ArrayList;
import java.util.List;

/**
 * Composite: treat individual objects and groups of objects through the same
 * interface, so client code can recurse over a tree without caring whether a
 * node is a leaf or a branch.
 * Java-specific angle: a sealed interface with `permits` (Java 17+) documents
 * the closed set of node types right in the type system, and pairs with an
 * exhaustive `switch` pattern match (module 08) as an alternative to virtual
 * dispatch for the recursive case.
 * Run:  java 11-design-patterns/Composite.java
 */
public class Composite {

    sealed interface FileSystemNode permits File, Directory {
        long size();
    }

    record File(String name, long size) implements FileSystemNode {
    }

    static final class Directory implements FileSystemNode {
        private final String name;
        private final List<FileSystemNode> children = new ArrayList<>();

        Directory(String name) { this.name = name; }

        Directory add(FileSystemNode child) {
            children.add(child);
            return this; // fluent, for readable setup below
        }

        @Override
        public long size() {
            long total = 0;
            for (FileSystemNode child : children) total += child.size(); // recurse into leaves and branches alike
            return total;
        }
    }

    public static void main(String[] args) {
        System.out.println("== composite ==");

        Directory root = new Directory("root")
            .add(new File("readme.md", 2_000))
            .add(new Directory("src")
                .add(new File("Main.java", 4_500))
                .add(new File("Util.java", 1_800)))
            .add(new Directory("target").add(new File("Main.class", 3_200)));

        System.out.println("  total size = " + root.size() + " bytes");
        System.out.println("  client called root.size() without knowing which children are files vs directories");
    }
}
