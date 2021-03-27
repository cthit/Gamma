package it.chalmers.gamma.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    private ResponseUtils() { }

    public static <T> ResponseEntity<T> toResponseObject(T responseObject) {
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

}
