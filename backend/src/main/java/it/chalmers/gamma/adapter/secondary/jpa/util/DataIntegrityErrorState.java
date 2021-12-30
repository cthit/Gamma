package it.chalmers.gamma.adapter.secondary.jpa.util;

public record DataIntegrityErrorState(String constraint, Type type) {

    public enum Type {
        NOT_UNIQUE("23505"), NOT_FOUND("23503"), IS_NULL("23502");

        Type(String errorCode) {
            this.ERROR_CODE = errorCode;
        }

        public final String ERROR_CODE;

    }

}
