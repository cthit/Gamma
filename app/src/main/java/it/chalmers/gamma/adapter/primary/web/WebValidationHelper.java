package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.validation.FailedValidation;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class WebValidationHelper {

  public static void validateObject(Object obj, BindingResult bindingResult) {
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      ValidatedWith validatedWith = field.getAnnotation(ValidatedWith.class);
      if (validatedWith != null) {
        field.setAccessible(true);
        try {
          Validator<Object> validator =
              (Validator<Object>) validatedWith.value().getDeclaredConstructor().newInstance();
          Object fieldValue = field.get(obj);
          ValidationResult validationResult = validator.validate(fieldValue);
          if (validationResult instanceof FailedValidation failedValidation) {
            bindingResult.addError(
                new FieldError(
                    "form",
                    field.getName(),
                    fieldValue,
                    true,
                    null,
                    null,
                    failedValidation.message()));
          }
        } catch (NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
