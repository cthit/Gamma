package it.chalmers.gamma.utils;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResponseUtils {

    public static ResultMatcher expectedStatus(boolean authorized) {
        if (authorized) {
            return status().is2xxSuccessful();
        }
        else return status().is4xxClientError();
    }

}
