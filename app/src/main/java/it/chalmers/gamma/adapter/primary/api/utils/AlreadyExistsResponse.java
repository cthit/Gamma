package it.chalmers.gamma.adapter.primary.api.utils;

import org.springframework.http.HttpStatus;

public abstract class AlreadyExistsResponse extends ErrorResponse {
  public AlreadyExistsResponse() {
    super(HttpStatus.CONFLICT);
  }
}
