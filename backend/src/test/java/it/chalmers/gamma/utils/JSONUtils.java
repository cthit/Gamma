package it.chalmers.gamma.utils;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

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
