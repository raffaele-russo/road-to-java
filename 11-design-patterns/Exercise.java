// Practice: implement the State pattern for a traffic light. Run once you think
// you're done:  java -ea 11-design-patterns/Exercise.java
public class Exercise {

    // TODO: turn this into `sealed interface LightState permits Red, Yellow, Green {}`
    interface LightState {
        int durationSeconds();
        LightState next();
    }

    // TODO: implement Red, Yellow, Green as records or classes implementing LightState.
    // Cycle: Red -> Green -> Yellow -> Red (a real light doesn't go straight from red to
    // yellow). Durations: Red = 30s, Green = 25s, Yellow = 5s.
    //
    //   record Red() implements LightState {
    //       public int durationSeconds() { return 30; }
    //       public LightState next() { return new Green(); }
    //   }
    //   ... Green -> Yellow, Yellow -> Red

    /** TODO: holds the current LightState (starts at Red), advance() moves to next(). */
    static class TrafficLight {
        // TODO: field + constructor (starts at Red) + advance() + current()
    }

    /** TODO: starting from light's CURRENT state, sum the durations of 3 consecutive
     *  states (the current one, then after advance(), then after advance() again) —
     *  a full cycle always visits all 3 states exactly once, so the sum should be the
     *  same (60) no matter which state the light started in. Mutates the light passed in. */
    static int totalCycleSeconds(TrafficLight light) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        TrafficLight fresh = new TrafficLight();
        assert fresh.current().durationSeconds() == 30 : "a new TrafficLight should start at Red (30s)";

        assert totalCycleSeconds(new TrafficLight()) == 60 : "a full cycle is always Red+Green+Yellow = 60s";

        TrafficLight advancedFirst = new TrafficLight();
        advancedFirst.advance();   // now at Green
        assert advancedFirst.current().durationSeconds() == 25 : "Red should advance to Green (25s)";
        assert totalCycleSeconds(advancedFirst) == 60 : "still 60s regardless of starting state";

        TrafficLight cycled = new TrafficLight();
        cycled.advance(); cycled.advance(); cycled.advance();
        assert cycled.current().durationSeconds() == 30 : "after 3 advances (a full cycle) should be back to Red";

        System.out.println("All good — module 11 exercise complete.");
    }
}
