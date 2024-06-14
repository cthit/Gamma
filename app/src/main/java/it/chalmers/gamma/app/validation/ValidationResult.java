package it.chalmers.gamma.app.validation;

public sealed interface ValidationResult permits FailedValidation, SuccessfulValidation {}
