package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.util.SerializerUtils;
import it.chalmers.gamma.views.WebsiteView;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.json.simple.JSONObject;

public class GoldappsUserSerializer {

    public JSONObject serialize(ITUser value,
                                @Nullable List<WebsiteView> websites,
                                @Nullable List<JSONObject> groups) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(true, value.getCid(), "cid"));
        values.add(new SerializerValue(true, value.getFirstName(), "first_name"));
        values.add(new SerializerValue(true, value.getLastName(), "second_name"));
        values.add(new SerializerValue(true, value.getNick(), "nick"));
        values.add(new SerializerValue(true, value.getEmail(), "mail"));
        values.add(new SerializerValue(true, value.isGdpr(), "gdpr_education"));
        return SerializerUtils.serialize(values, false);
    }

}

