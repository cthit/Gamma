package it.chalmers.gamma.adapter.primary.api.v2;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class V2NotFoundResponse extends ResponseStatusException {
  public V2NotFoundResponse() {
    super(HttpStatus.NOT_FOUND);
  }
}
