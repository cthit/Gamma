package it.chalmers.gamma.adapter.primary.web;

import java.nio.charset.StandardCharsets;

/**
 * Builds {@code mailto:} hrefs for email addresses.
 *
 * <p>RFC 6068 section 2 only allows unreserved characters and {@code ! $ ' ( ) * + : @} to appear
 * unencoded in the recipient. Everything else (for example {@code # ? / = & ; %}, which are all
 * valid in an email local part) must be percent-encoded, otherwise the address is cut short or
 * interpreted as a header field. {@code ,} separates recipients and is encoded as well.
 */
public final class MailtoLink {

  private static final String ALLOWED = "-._~!$'()*+:@";

  private static final String ALLOWED_IN_HEADER_VALUE = "-._~!$'()*:@";

  private static final char[] HEX = "0123456789ABCDEF".toCharArray();

  private MailtoLink() {}

  public static String href(String email) {
    return "mailto:" + encode(email, ALLOWED);
  }

  public static String href(String email, String subject, String body) {
    return href(email)
        + "?subject="
        + encode(subject, ALLOWED_IN_HEADER_VALUE)
        + "&body="
        + encode(body.replace("\r\n", "\n").replace("\n", "\r\n"), ALLOWED_IN_HEADER_VALUE);
  }

  private static String encode(String value, String allowed) {
    StringBuilder encoded = new StringBuilder();
    for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
      char c = (char) (b & 0xff);
      if ((c >= 'a' && c <= 'z')
          || (c >= 'A' && c <= 'Z')
          || (c >= '0' && c <= '9')
          || allowed.indexOf(c) >= 0) {
        encoded.append(c);
      } else {
        encoded.append('%').append(HEX[(c >> 4) & 0xf]).append(HEX[c & 0xf]);
      }
    }
    return encoded.toString();
  }
}
