/**
 * Adapter: wrap an incompatible existing class behind the interface a client expects,
 * without modifying either side. Common at legacy/third-party API boundaries.
 * Run:  java 11-design-patterns/Adapter.java
 */
public class Adapter {

    // The interface our (new) client code expects.
    interface JsonSerializer {
        String toJson(Object data);
    }

    // A pre-existing, third-party-style class we can't change and whose
    // interface doesn't match JsonSerializer.
    static class LegacyXmlLibrary {
        String convertToXml(Object data) {
            return "<data>" + data + "</data>";
        }
    }

    // The adapter: implements the target interface, delegates to the incompatible one.
    static class XmlToJsonAdapter implements JsonSerializer {
        private final LegacyXmlLibrary legacy;
        XmlToJsonAdapter(LegacyXmlLibrary legacy) { this.legacy = legacy; }

        @Override
        public String toJson(Object data) {
            String xml = legacy.convertToXml(data);            // delegate to incompatible API
            return "{\"xmlPayload\": \"" + xml.replace("\"", "\\\"") + "\"}"; // adapt the result
        }
    }

    static void printReport(JsonSerializer serializer, Object data) {
        System.out.println("  " + serializer.toJson(data));    // client only knows JsonSerializer
    }

    public static void main(String[] args) {
        System.out.println("== adapter ==");
        JsonSerializer adapted = new XmlToJsonAdapter(new LegacyXmlLibrary());
        printReport(adapted, "hello");
        System.out.println("  client code depends only on JsonSerializer, never on LegacyXmlLibrary");
    }
}
