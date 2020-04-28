package it.chalmers.gamma.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JSONUtils {
    public static String objectToJSONString(Object o) {
        try {
            System.out.println(o);
            return new ObjectMapper().writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
