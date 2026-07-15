import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Practice: implement MultiMap and LruCache below. Run once you think you're done:
//   java -ea 03-collections-generics/Exercise.java
public class Exercise {

    /** TODO: a generic multimap — one key maps to a growable list of values.
     *  Wrap a Map<K, List<V>> internally. put() should create the list on first use
     *  (computeIfAbsent, no manual "if absent, create" branching). get() on a missing
     *  key should return an empty (not null) list. */
    static class MultiMap<K, V> {
        private final Map<K, List<V>> data = new java.util.HashMap<>();

        void put(K key, V value) {
            data.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        List<V> get(K key) {
            return data.containsKey(key) ? List.copyOf(data.get(key)) : List.of();
        }

        int size() {
            // number of KEYS, not total values
            return data.size();
        }
    }

    /** TODO: a fixed-capacity LRU cache. Extend LinkedHashMap with access-order = true
     *  (the 3-arg constructor), and override removeEldestEntry to evict once size()
     *  exceeds capacity. Do not implement your own linked-list eviction logic. */
    static class LruCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        LruCache(int capacity) {
            super(Math.max(1, capacity), .75f, true);
            if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
            this.capacity = capacity;
        }

        // TODO: override removeEldestEntry(Map.Entry<K, V> eldest)
        @Override 
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest){
            return size() > capacity;
        }
    }

    public static void main(String[] args) {
        MultiMap<String, Integer> mm = new MultiMap<>();
        mm.put("a", 1);
        mm.put("a", 2);
        mm.put("b", 3);
        assert mm.get("a").equals(List.of(1, 2)) : "got: " + mm.get("a");
        assert mm.get("b").equals(List.of(3));
        assert mm.get("missing").isEmpty() : "missing key should give an empty list, not null";
        assert mm.size() == 2 : "size() counts keys, got " + mm.size();

        LruCache<Integer, String> cache = new LruCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);              // touch 1 -> makes 2 the least-recently-used
        cache.put(3, "c");         // capacity exceeded -> evict 2, the LRU entry
        assert cache.containsKey(1) : "1 was recently accessed, should survive";
        assert !cache.containsKey(2) : "2 was the LRU entry, should have been evicted";
        assert cache.containsKey(3);
        assert cache.size() == 2 : "cache must never exceed capacity";
        

        System.out.println("All good — module 03 exercise complete.");
    }
}
