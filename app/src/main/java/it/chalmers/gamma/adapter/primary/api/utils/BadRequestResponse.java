package it.chalmers.gamma.adapter.primary.api.utils;

import org.springframework.http.HttpStatus;

public class BadRequestResponse extends ErrorResponse {
  public BadRequestResponse() {
    super(HttpStatus.BAD_REQUEST);
  }
}
