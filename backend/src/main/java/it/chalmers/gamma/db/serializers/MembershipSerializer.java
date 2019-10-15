package it.chalmers.gamma.db.serializers;


import com.sun.istack.Nullable;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.util.SerializerUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MembershipSerializer {
    private final List<Properties> properties;

    public JSONObject serialize(Membership value) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(
                this.properties.contains(Properties.UNOFFICIAL_POST_NAME),
                value.getUnofficialPostName(), "unofficialPostName"));
        values.add(new SerializerValue(
                this.properties.contains(Properties.USER_CID), value.getId().getITUser().getCid(), "userCid"));
        values.add(new SerializerValue(
                this.properties.contains(Properties.USER_ID), value.getId().getITUser().getId(), "userId"));
        values.add(new SerializerValue(
                this.properties.contains(Properties.USER_NICK),
                value.getId().getITUser().getNick(), "userNick"));
        values.add(new SerializerValue(
                this.properties.contains(Properties.GROUP_ID), value.getId().getFKITGroup().getId(), "groupId"));
        values.add(new SerializerValue(
                this.properties.contains(Properties.GROUP_NAME),
                value.getId().getFKITGroup().getPrettyName(), "groupName"));
        values.add(new SerializerValue(
                this.properties.contains(Properties.POST_NAME), value.getId().getPost(), "officialPost"));
        return SerializerUtils.serialize(values, false);
    }

    public MembershipSerializer(List<Properties> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public enum Properties {
        UNOFFICIAL_POST_NAME,
        USER_CID,
        USER_ID,
        USER_NICK,
        GROUP_ID,
        GROUP_NAME,
        POST_NAME;

        public static List<Properties> getAllProperties() {
            Properties[] props = {
                    UNOFFICIAL_POST_NAME,
                    USER_CID,
                    USER_ID,
                    USER_NICK,
                    GROUP_ID,
                    GROUP_NAME,
                    POST_NAME
            };
            return new ArrayList<>(Arrays.asList(props));
        }
    }
}
