public class Exercise {
    static String classEntry(String binaryName) {
        throw new UnsupportedOperationException("TODO: convert a binary name to a JAR entry");
    }

    static boolean canRun(int classFileRelease, int runtimeRelease) {
        throw new UnsupportedOperationException("TODO: reject newer class files on older runtimes");
    }

    public static void main(String[] args) {
        assert classEntry("com.example.Order").equals("com/example/Order.class");
        assert canRun(17, 25);
        assert !canRun(25, 21);
    }
}
