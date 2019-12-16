package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class GoldappsUserSerializer {

    public JSONObject serialize(ITUserDTO value) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(true, value.getCid(), "cid"));
        values.add(new SerializerValue(true, value.getFirstName(), "first_name"));
        values.add(new SerializerValue(true, value.getLastName(), "second_name"));
        values.add(new SerializerValue(true, value.getNick(), "nick"));
        values.add(new SerializerValue(true, value.getEmail(), "mail"));
        values.add(new SerializerValue(true, value.isGdpr(), "gdpr_education"));
        return SerializerUtils.serialize(values, false);
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

