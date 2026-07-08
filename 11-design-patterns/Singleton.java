/**
 * Singleton: eager, lazy double-checked-locking, and enum (recommended) forms.
 * Run:  java 11-design-patterns/Singleton.java
 */
public class Singleton {

    // ---- 1. Eager initialization: simple, thread-safe, but instance created even if unused ----
    static class EagerSingleton {
        private static final EagerSingleton INSTANCE = new EagerSingleton();
        private EagerSingleton() {}
        static EagerSingleton getInstance() { return INSTANCE; }
    }

    // ---- 2. Lazy + double-checked locking: thread-safe, created on first use ----
    // volatile is REQUIRED: without it another thread could see a partially
    // constructed object due to instruction reordering allowed by the JMM.
    static class LazySingleton {
        private static volatile LazySingleton instance;
        private LazySingleton() {}
        static LazySingleton getInstance() {
            if (instance == null) {                 // first check, no locking (fast path)
                synchronized (LazySingleton.class) {
                    if (instance == null) {          // second check, inside the lock
                        instance = new LazySingleton();
                    }
                }
            }
            return instance;
        }
    }

    // ---- 3. Enum singleton: RECOMMENDED — free serialization & reflection safety ----
    enum EnumSingleton {
        INSTANCE;
        private int counter = 0;
        void increment() { counter++; }
        int count() { return counter; }
    }

    public static void main(String[] args) {
        System.out.println("== eager ==");
        System.out.println("  same instance: " + (EagerSingleton.getInstance() == EagerSingleton.getInstance()));

        System.out.println("\n== lazy double-checked locking ==");
        System.out.println("  same instance: " + (LazySingleton.getInstance() == LazySingleton.getInstance()));

        System.out.println("\n== enum singleton (recommended) ==");
        EnumSingleton.INSTANCE.increment();
        EnumSingleton.INSTANCE.increment();
        System.out.println("  counter (shared state): " + EnumSingleton.INSTANCE.count()); // 2
        System.out.println("  same instance: " + (EnumSingleton.INSTANCE == EnumSingleton.valueOf("INSTANCE")));
    }
}
