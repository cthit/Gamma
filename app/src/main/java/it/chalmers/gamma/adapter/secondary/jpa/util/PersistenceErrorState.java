package it.chalmers.gamma.adapter.secondary.jpa.util;

public record PersistenceErrorState(String constraint, Type type) {

  public enum Type {
    NOT_UNIQUE("23505"),
    FOREIGN_KEY_VIOLATION("23503"),
    IS_NULL("23502");

    public final String ERROR_CODE;

    Type(String errorCode) {
      this.ERROR_CODE = errorCode;
    }
  }
}
