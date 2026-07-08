/**
 * Proxy: stand in for another object to control access to it — lazy-load an
 * expensive resource, check permissions, add logging/caching — without the
 * client knowing it isn't talking to the real thing.
 * This is the hand-rolled "static proxy" form. Java also has a built-in dynamic
 * proxy mechanism (java.lang.reflect.Proxy) that generates the proxy class at
 * runtime from an interface — that's what powers Spring AOP and Hibernate's
 * lazy-loaded entity proxies, so you rarely hand-write one in real Java code.
 * Run:  java 11-design-patterns/Proxy.java
 */
public class Proxy {

    interface ReportGenerator {
        String generate(String reportId);
    }

    // The real, expensive object — pretend this takes a while to construct/run.
    static class RealReportGenerator implements ReportGenerator {
        RealReportGenerator() {
            System.out.println("  [real] expensive setup for report engine...");
        }

        @Override
        public String generate(String reportId) {
            return "report[" + reportId + "]=42 pages";
        }
    }

    // Virtual proxy: defers creating the real object until it's actually needed,
    // and adds a cache so repeated requests skip the real work entirely.
    static class CachingReportProxy implements ReportGenerator {
        private RealReportGenerator real; // not created until first use
        private final java.util.Map<String, String> cache = new java.util.HashMap<>();

        @Override
        public String generate(String reportId) {
            if (cache.containsKey(reportId)) {
                System.out.println("  [proxy] cache hit for " + reportId);
                return cache.get(reportId);
            }
            if (real == null) real = new RealReportGenerator(); // lazy init
            String result = real.generate(reportId);
            cache.put(reportId, result);
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("== proxy ==");
        ReportGenerator reports = new CachingReportProxy();
        System.out.println("  " + reports.generate("Q1"));  // triggers real generator, caches
        System.out.println("  " + reports.generate("Q1"));  // served from cache, no real work
        System.out.println("  client only depends on ReportGenerator, never sees RealReportGenerator directly");
    }
}
