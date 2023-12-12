package it.chalmers.gamma.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.ResultMatcher;

public final class ResponseUtils {

    private ResponseUtils() {

    }

    public static ResultMatcher expectedStatus(boolean authorized) {
        if (authorized) {
            return status().is2xxSuccessful();
        } else {
            return status().is4xxClientError();
        }
    }


}
