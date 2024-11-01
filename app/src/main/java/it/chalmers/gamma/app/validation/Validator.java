package it.chalmers.gamma.app.validation;

public interface Validator<T> {
  ValidationResult validate(T value);
}
