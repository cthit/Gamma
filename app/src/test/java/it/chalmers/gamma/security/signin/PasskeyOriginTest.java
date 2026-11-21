package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import org.junit.jupiter.api.Test;

class PasskeyOriginTest {

  @Test
  void aDefaultPortIsLeftOutLikeBrowsersDo() {
    assertEquals("https://gamma.example.org", origin("https://gamma.example.org:443"));
    assertEquals("http://localhost", origin("http://localhost:80/"));
    assertEquals("https://gamma.example.org", origin("https://Gamma.Example.org/"));
  }

  @Test
  void anyOtherPortIsKept() {
    assertEquals("http://localhost:8080", origin("http://localhost:8080"));
    assertEquals("https://gamma.example.org:8443", origin("https://gamma.example.org:8443"));
    assertEquals("http://gamma.example.org:443", origin("http://gamma.example.org:443"));
  }

  private static String origin(String baseUrl) {
    return PasskeyConfig.originOf(URI.create(baseUrl));
  }
}
