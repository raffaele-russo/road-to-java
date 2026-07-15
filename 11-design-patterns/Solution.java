/** Reference solution for the State-pattern exercise. */
public class Solution {
    sealed interface LightState permits Red, Green, Yellow {
        int durationSeconds();
        LightState next();
    }
    record Red() implements LightState {
        public int durationSeconds() { return 30; }
        public LightState next() { return new Green(); }
    }
    record Green() implements LightState {
        public int durationSeconds() { return 25; }
        public LightState next() { return new Yellow(); }
    }
    record Yellow() implements LightState {
        public int durationSeconds() { return 5; }
        public LightState next() { return new Red(); }
    }
    static class TrafficLight {
        private LightState current = new Red();
        LightState current() { return current; }
        void advance() { current = current.next(); }
    }
    static int totalCycleSeconds(TrafficLight light) {
        int total = 0;
        for (int i = 0; i < 3; i++) {
            total += light.current().durationSeconds();
            light.advance();
        }
        return total;
    }
    public static void main(String[] args) {
        assert totalCycleSeconds(new TrafficLight()) == 60;
        System.out.println("Module 11 reference solution passed.");
    }
}
