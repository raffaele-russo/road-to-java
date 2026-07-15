import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

public class EssentialApis {
    record Money(BigDecimal amount, String currency) {
        Money {
            if (amount.signum() < 0) throw new IllegalArgumentException("negative money");
            amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        }
        Money add(Money other) {
            if (!currency.equals(other.currency)) throw new IllegalArgumentException("currency mismatch");
            return new Money(amount.add(other.amount), currency);
        }
    }

    public static void main(String[] args) {
        Money total = new Money(new BigDecimal("0.10"), "EUR")
            .add(new Money(new BigDecimal("0.20"), "EUR"));
        assert total.amount().equals(new BigDecimal("0.30"));

        Clock fixed = Clock.fixed(Instant.parse("2026-07-15T10:00:00Z"), ZoneId.of("UTC"));
        assert Instant.now(fixed).equals(Instant.parse("2026-07-15T10:00:00Z"));
        System.out.println(ZonedDateTime.now(fixed).withZoneSameInstant(ZoneId.of("Europe/Paris")));

        String musicalSymbol = "\uD834\uDD1E";
        assert musicalSymbol.length() == 2 && musicalSymbol.codePointCount(0, musicalSymbol.length()) == 1;
        assert Pattern.compile("[A-Z]{3}-\\d{4}").matcher("ORD-2026").matches();
        assert UUID.fromString(UUID.randomUUID().toString()) != null;

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://example.invalid/orders"))
            .timeout(Duration.ofSeconds(2)).GET().build();
        assert request.timeout().orElseThrow().equals(Duration.ofSeconds(2));
        System.out.println("Essential API contracts passed.");
    }
}
