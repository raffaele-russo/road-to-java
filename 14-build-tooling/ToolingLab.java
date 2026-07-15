/** Deterministic observations about class names, modules, and release compatibility. */
public class ToolingLab {
    static String classEntry(Class<?> type) {
        return type.getName().replace('.', '/') + ".class";
    }

    static boolean canRun(int classFileRelease, int runtimeRelease) {
        return classFileRelease <= runtimeRelease;
    }

    public static void main(String[] args) {
        assert classEntry(java.net.http.HttpClient.class).equals("java/net/http/HttpClient.class");
        assert java.net.http.HttpClient.class.getModule().getName().equals("java.net.http");
        assert !ToolingLab.class.getModule().isNamed(); // source-file mode uses the unnamed module
        assert canRun(21, 25);
        assert !canRun(25, 21);
        System.out.println("classpath entry, module identity, and release checks passed");
    }
}
