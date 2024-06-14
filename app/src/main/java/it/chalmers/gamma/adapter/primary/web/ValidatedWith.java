package it.chalmers.gamma.adapter.primary.web;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import it.chalmers.gamma.app.validation.Validator;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ValidatedWith {
  Class<? extends Validator<?>> value();
}
