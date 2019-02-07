package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.util.SerializerUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class GoldappsGroupSerializer {

    public JSONObject serialize(FKITGroup value,
                                List<String> groupMembers) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(true, value.getEmail(), "email"));
        values.add(new SerializerValue(true, groupMembers, "members"));
        values.add(new SerializerValue(true, null, "aliases"));
        return SerializerUtils.serialize(values, false);
    }
}
