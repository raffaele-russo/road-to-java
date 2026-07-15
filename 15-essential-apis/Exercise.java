import java.math.BigDecimal;
import java.time.*;

public class Exercise {
    static BigDecimal lineTotal(String price, int quantity) {
        throw new UnsupportedOperationException("TODO: exact decimal multiplication");
    }

    static int codePointCount(String text) {
        throw new UnsupportedOperationException("TODO: do not count UTF-16 code units");
    }

    static int validOffsets(LocalDateTime local, ZoneId zone) {
        throw new UnsupportedOperationException("TODO: ask the zone rules");
    }

    public static void main(String[] args) {
        assert lineTotal("0.10", 3).compareTo(new BigDecimal("0.30")) == 0;
        assert codePointCount("A😀́") == 3;
        assert validOffsets(LocalDateTime.of(2024, 10, 27, 2, 30), ZoneId.of("Europe/Paris")) == 2;
    }
}
