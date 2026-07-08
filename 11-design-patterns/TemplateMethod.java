/**
 * Template Method: define the skeleton of an algorithm in a base class, and let
 * subclasses override individual steps without changing the overall structure.
 * Java-specific angle: this is one of the few remaining places Java's single
 * inheritance model is load-bearing — the fixed skeleton lives in one abstract
 * class, so a subclass can only ever override its steps, never rearrange them.
 * Contrast with Strategy: Strategy swaps a whole algorithm via composition;
 * Template Method fixes the algorithm's shape and overrides pieces via inheritance.
 * Run:  java 11-design-patterns/TemplateMethod.java
 */
public class TemplateMethod {

    abstract static class DataProcessor {
        // The template method: fixed sequence, marked final so subclasses can't reorder it.
        final void process() {
            String raw = readData();
            String transformed = transform(raw);
            writeData(transformed);
        }

        abstract String readData();
        abstract String transform(String raw);

        // A step with a sensible default — subclasses may override it, but don't have to.
        void writeData(String data) {
            System.out.println("  [default sink] " + data);
        }
    }

    static class CsvProcessor extends DataProcessor {
        @Override
        String readData() { return "a,b,c"; }

        @Override
        String transform(String raw) { return raw.replace(",", " | "); }
    }

    static class JsonProcessor extends DataProcessor {
        @Override
        String readData() { return "{\"a\":1}"; }

        @Override
        String transform(String raw) { return raw.toUpperCase(); }

        @Override
        void writeData(String data) {
            System.out.println("  [json sink] " + data); // overrides the default step
        }
    }

    public static void main(String[] args) {
        System.out.println("== template method ==");
        new CsvProcessor().process();
        new JsonProcessor().process();
        System.out.println("  both ran the same fixed skeleton (read -> transform -> write) with different steps");
    }
}
