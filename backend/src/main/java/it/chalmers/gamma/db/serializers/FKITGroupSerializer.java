package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.util.SerializerUtils;
import it.chalmers.gamma.views.WebsiteView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.json.simple.JSONObject;

public class FKITGroupSerializer {

    public enum Properties {
        ID,
        AVATAR_URL,
        NAME,
        PRETTY_NAME,
        DESCRIPTION,
        FUNC,
        EMAIL,
        TYPE,
        WEBSITES,
        USERS,
        SUPER_GROUP;

        public static List<Properties> getAllProperties() {
            Properties[] props = {
                ID, AVATAR_URL, NAME, PRETTY_NAME, DESCRIPTION, FUNC, EMAIL, TYPE, WEBSITES, USERS, SUPER_GROUP
            };
            return new ArrayList<>(Arrays.asList(props));
        }
    }

    private final List<Properties> properties;

    public FKITGroupSerializer(List<Properties> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public JSONObject serialize(
            FKITGroup value,
            @Nullable List<JSONObject> groupMembers,
            @Nullable List<WebsiteView> websites) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(
                this.properties.contains(Properties.ID), value.getId(), "id")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.NAME), value.getName(), "name")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.DESCRIPTION), value.getDescription(), "description")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.FUNC), value.getFunc(), "function")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.EMAIL), value.getEmail(), "email")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.PRETTY_NAME), value.getPrettyName(), "pretty_name")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.AVATAR_URL), value.getAvatarURL(), "avatar_url")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.USERS), groupMembers, "group_members")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.WEBSITES), websites, "websites")
        );
        values.add(new SerializerValue(
                this.properties.contains(Properties.SUPER_GROUP), value.getSuperGroup(), "super"
        ));
        return SerializerUtils.serialize(values, false);

    }
}
