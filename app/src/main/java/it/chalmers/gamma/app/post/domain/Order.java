package it.chalmers.gamma.app.post.domain;

public record Order(int value) {

  public Order {
    if (value < 0) {
      throw new IllegalArgumentException("order must be >= 0");
    }
  }
}
