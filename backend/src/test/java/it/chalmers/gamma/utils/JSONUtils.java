package it.chalmers.gamma.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.chalmers.gamma.Endoints.JSONParameter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static String toJsonTemplate(JSONParameter... rawParameters) {
        List<JSONParameter> parameters = (Arrays.asList(rawParameters));
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for(JSONParameter parameter : parameters) {
            builder.append(String.format("\"%s\": \"%s\"%s",
                    parameter.getKey(),
                    parameter.getValue(),
                    parameter.equals(parameters.get(parameters.size() - 1)) ? "\n}" : ",\n"
                    ));
        }
        return builder.toString();
    }

    public static String toFormUrlEncoded(JSONParameter... rawParameters) {
        List<JSONParameter> parameters = (Arrays.asList(rawParameters));
        StringBuilder builder = new StringBuilder();
        for (JSONParameter parameter : parameters) {
            builder.append(String.format("%s=%s%s",
                    parameter.getKey(),
                    parameter.getValue(),
                    parameter.equals(parameters.get(parameters.size() - 1)) ? "" : "&"
            ));
        }
        return builder.toString();
    }
}
