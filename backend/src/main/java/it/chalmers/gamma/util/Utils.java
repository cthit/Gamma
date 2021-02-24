package it.chalmers.gamma.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {

    public static <T> ResponseEntity<T> toResponseObject(T responseObject) {
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    public static String classToScreamingSnakeCase(Class<?> c) {
        String className = c.getSimpleName();
        String[] camelCaseWords = className.split("(?=[A-Z])");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < camelCaseWords.length - 1; i++) {
            sb.append(camelCaseWords[i].toUpperCase());
            sb.append("_");
        }

        sb.append(camelCaseWords[camelCaseWords.length - 1].toUpperCase());

        return sb.toString();

    }

}
