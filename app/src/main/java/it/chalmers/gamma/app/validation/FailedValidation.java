package it.chalmers.gamma.app.validation;

public record FailedValidation(String message) implements ValidationResult {}
