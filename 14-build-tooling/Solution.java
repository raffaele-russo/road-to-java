public class Solution {
    static String classEntry(String binaryName) {
        if (binaryName == null || binaryName.isBlank()) throw new IllegalArgumentException("binaryName");
        return binaryName.replace('.', '/') + ".class";
    }

    static boolean canRun(int classFileRelease, int runtimeRelease) {
        if (classFileRelease < 1 || runtimeRelease < 1) throw new IllegalArgumentException("release");
        return classFileRelease <= runtimeRelease;
    }

    public static void main(String[] args) {
        assert classEntry("com.example.Order").equals("com/example/Order.class");
        assert canRun(17, 25);
        assert !canRun(25, 21);
        System.out.println("tooling solution passed");
    }
}
