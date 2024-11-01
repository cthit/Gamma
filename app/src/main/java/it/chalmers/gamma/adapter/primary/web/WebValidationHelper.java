package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.validation.FailedValidation;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class WebValidationHelper {

  public static void validateObject(Object obj, BindingResult bindingResult) {
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      validateField(field, "", obj, bindingResult);
    }
  }

  private static void validateField(
      Field field, String prefix, Object obj, BindingResult bindingResult) {
    if (Modifier.isStatic(field.getModifiers())) {
      return;
    }

    field.setAccessible(true);
    Object fieldValue;
    try {
      fieldValue = field.get(obj);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    ValidatedWith validatedWith = field.getAnnotation(ValidatedWith.class);
    if (validatedWith != null) {
      try {
        Validator<Object> validator =
            (Validator<Object>) validatedWith.value().getDeclaredConstructor().newInstance();
        ValidationResult validationResult = validator.validate(fieldValue);
        if (validationResult instanceof FailedValidation failedValidation) {
          bindingResult.addError(
              new FieldError(
                  "form",
                  prefix + field.getName(),
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

    if (fieldValue instanceof Iterable<?>) {
      int i = 0;
      for (Object item : (Iterable<?>) fieldValue) {
        if (item != null
            && !item.getClass().isPrimitive()
            && !(item instanceof String)
            && !item.getClass().getPackageName().startsWith("java")) {
          String newPrefix = String.format("%s%s[%d].", prefix, field.getName(), i);
          validateObject(item, newPrefix, bindingResult);
        }
        i++;
      }
    }
  }

  private static void validateObject(Object obj, String prefix, BindingResult bindingResult) {
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      validateField(field, prefix, obj, bindingResult);
    }
  }
}
