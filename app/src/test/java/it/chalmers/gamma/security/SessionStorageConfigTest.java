package it.chalmers.gamma.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthentication;

class SessionStorageConfigTest {

  private final RedisSerializer<Object> serializer =
      new SessionStorageConfig().springSessionDefaultRedisSerializer();

  @Test
  void stripsNonSerializableDetailsFromWebAuthnAuthentication() {
    var principal =
        ImmutablePublicKeyCredentialUserEntity.builder()
            .id(Bytes.fromBase64("AQIDBA"))
            .name("user-id")
            .displayName("User")
            .build();
    var authority =
        FactorGrantedAuthority.withAuthority(FactorGrantedAuthority.WEBAUTHN_AUTHORITY)
            .issuedAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();
    var token = new WebAuthnAuthentication(principal, List.of(authority));
    Object nonSerializableDetails = new Object();
    token.setDetails(nonSerializableDetails);

    byte[] bytes = serializer.serialize(new SecurityContextImpl(token));
    SecurityContext deserialized =
        assertInstanceOf(SecurityContext.class, serializer.deserialize(bytes));
    WebAuthnAuthentication restored =
        assertInstanceOf(WebAuthnAuthentication.class, deserialized.getAuthentication());

    assertEquals(principal, restored.getPrincipal());
    assertEquals(List.of(authority), List.copyOf(restored.getAuthorities()));
    assertNull(restored.getDetails());
    assertNotNull(token.getDetails());
  }

  @Test
  void stripsNonSerializableDetailsFromUsernamePasswordAuthentication() {
    var authority =
        FactorGrantedAuthority.withAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY)
            .issuedAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();
    var token =
        UsernamePasswordAuthenticationToken.authenticated("user-id", null, List.of(authority));
    token.setDetails(new Object());

    byte[] bytes = serializer.serialize(new SecurityContextImpl(token));
    SecurityContext deserialized =
        assertInstanceOf(SecurityContext.class, serializer.deserialize(bytes));
    UsernamePasswordAuthenticationToken restored =
        assertInstanceOf(
            UsernamePasswordAuthenticationToken.class, deserialized.getAuthentication());

    assertEquals("user-id", restored.getPrincipal());
    assertEquals(List.of(authority), List.copyOf(restored.getAuthorities()));
    assertNull(restored.getDetails());
    assertNotNull(token.getDetails());
  }
}
