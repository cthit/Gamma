package it.chalmers.gamma.app.signin;

import static org.junit.jupiter.api.Assertions.*;

import it.chalmers.gamma.app.signin.domain.TotpSecret;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TotpCodesTest {

  // RFC 6238 Appendix B's SHA-1 secret, with its codes truncated to Gamma's six digits.
  private static final TotpSecret SECRET = new TotpSecret("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ");

  @ParameterizedTest
  @CsvSource({
    "59, 287082",
    "1111111109, 081804",
    "1111111111, 050471",
    "1234567890, 005924",
    "2000000000, 279037",
    "20000000000, 353130"
  })
  void acceptsRfcCodesIncludingLeadingZerosAndDatesAfter2038(long seconds, String code) {
    assertEquals(
        TotpCodes.timeStep(Instant.ofEpochSecond(seconds)),
        TotpCodes.matchingTimeStep(SECRET, code, Instant.ofEpochSecond(seconds)).orElseThrow());
  }

  @Test
  void acceptsOnlyTheCurrentAndImmediatelyAdjacentTimeSteps() {
    long step = TotpCodes.timeStep(Instant.ofEpochSecond(1234567890));
    for (long offset = -3; offset <= 3; offset++) {
      var match =
          TotpCodes.matchingTimeStep(SECRET, "005924", Instant.ofEpochSecond((step + offset) * 30));
      if (Math.abs(offset) <= 1) {
        assertEquals(step, match.orElseThrow());
      } else {
        assertTrue(match.isEmpty());
      }
    }
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"28708", "0287082", "abcdef", "+287082", "287-082"})
  void rejectsMalformedCodesWithoutThrowing(String code) {
    assertTrue(TotpCodes.matchingTimeStep(SECRET, code, Instant.ofEpochSecond(59)).isEmpty());
  }

  @Test
  void permitsWhitespaceUsedToGroupDigits() {
    assertEquals(
        1,
        TotpCodes.matchingTimeStep(SECRET, " 287 082 ", Instant.ofEpochSecond(59)).orElseThrow());
  }
}
