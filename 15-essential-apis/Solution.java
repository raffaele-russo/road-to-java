import java.math.BigDecimal;
import java.time.*;

public class Solution {
    static BigDecimal lineTotal(String price, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity");
        return new BigDecimal(price).multiply(BigDecimal.valueOf(quantity));
    }

    static int codePointCount(String text) {
        return text.codePointCount(0, text.length());
    }

    static int validOffsets(LocalDateTime local, ZoneId zone) {
        return zone.getRules().getValidOffsets(local).size();
    }

    public static void main(String[] args) {
        assert lineTotal("0.10", 3).compareTo(new BigDecimal("0.30")) == 0;
        assert codePointCount("A😀́") == 3;
        assert validOffsets(LocalDateTime.of(2024, 10, 27, 2, 30), ZoneId.of("Europe/Paris")) == 2;
        System.out.println("essential APIs solution passed");
    }
}
