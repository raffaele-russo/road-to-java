import java.lang.management.ManagementFactory;

/**
 * Peeks at the running JVM: heap, GC activity, cores. A concrete way to connect
 * the theory in the README to your actual runtime.
 * Run:  java 07-jvm-memory/JvmInfo.java
 */
public class JvmInfo {

    public static void main(String[] args) {
        runtimeInfo();
        heapUsage();
        demonstrateGc();
    }

    static void runtimeInfo() {
        System.out.println("== runtime ==");
        Runtime rt = Runtime.getRuntime();
        System.out.println("  Java version : " + System.getProperty("java.version"));
        System.out.println("  Cores        : " + rt.availableProcessors());
        System.out.printf("  Max heap     : %d MB%n", rt.maxMemory() / (1024 * 1024));

        System.out.println("  GC collectors:");
        ManagementFactory.getGarbageCollectorMXBeans()
            .forEach(gc -> System.out.println("    - " + gc.getName()));
    }

    static void heapUsage() {
        System.out.println("\n== heap before/after allocation ==");
        Runtime rt = Runtime.getRuntime();
        long before = used(rt);
        // Allocate ~10M ints (~40MB) to move the needle.
        int[][] blocks = new int[10][];
        for (int i = 0; i < blocks.length; i++) blocks[i] = new int[1_000_000];
        long after = used(rt);
        System.out.printf("  used before: %d MB%n", before / (1024 * 1024));
        System.out.printf("  used after : %d MB%n", after / (1024 * 1024));
        System.out.println("  (blocks kept reachable so GC can't reclaim yet)");
    }

    static void demonstrateGc() {
        System.out.println("\n== garbage becomes collectible when unreachable ==");
        Object o = new Object();
        System.out.println("  strong ref held: " + (o != null));
        o = null;                 // now unreachable -> eligible for GC
        System.gc();              // a hint, not a guarantee
        System.out.println("  ref dropped + System.gc() hint issued (JVM decides when)");
    }

    static long used(Runtime rt) {
        return rt.totalMemory() - rt.freeMemory();
    }
}
