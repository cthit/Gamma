package it.chalmers.gamma.db.serializers;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class GoldappsGroupSerializer {

    public JSONObject serialize(String value,
                                List<String> groupMembers) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(true, value, "email"));
        values.add(new SerializerValue(true, groupMembers, "members"));
        values.add(new SerializerValue(true, null, "aliases"));
        return SerializerUtils.serialize(values, true);
    }
    private static class SerializerValue {

        private final boolean enabled;
        private final Object value;
        private final String name;

        private SerializerValue(boolean enabled, Object value, String name) {
            this.enabled = enabled;
            this.value = value;
            this.name = name;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public Object getValue() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }


private static class SerializerUtils {


    private static JSONObject serialize(List<SerializerValue> values, boolean includeNullFields) {
        JSONObject json = new JSONObject();
        for (SerializerValue value : values) {
            if (value.isEnabled() && !(!includeNullFields && value.getValue() == null)) {
                json.put(value.getName(), value.getValue());
            }
        }
        return json;
    }
    }
}
