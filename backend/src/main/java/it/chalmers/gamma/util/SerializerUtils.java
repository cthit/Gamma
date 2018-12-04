package it.chalmers.gamma.util;

import it.chalmers.gamma.db.serializers.SerializerValue;

import java.util.List;

import org.json.simple.JSONObject;


public final class SerializerUtils {

    private SerializerUtils() {
    }

    public static JSONObject serialize(List<SerializerValue> values, boolean includeNullFields) {
        JSONObject json = new JSONObject();
        for (SerializerValue value : values) {
            if (value.isEnabled() && !(!includeNullFields && value.getValue() == null)) {
                json.put(value.getName(), value.getValue());
            }
        }
        return json;
    }


}
