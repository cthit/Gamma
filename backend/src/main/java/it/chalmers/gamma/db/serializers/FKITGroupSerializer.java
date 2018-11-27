package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.service.EntityWebsiteService;
import it.chalmers.gamma.util.SerializerUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.json.simple.JSONObject;

public class FKITGroupSerializer {

    public enum Properties {
        ID(false),
        AVATAR_URL(false),
        NAME(false),
        PRETTY_NAME(false),
        DESCRIPTION(false),
        FUNC(false),
        EMAIL(false),
        TYPE(false),
        WEBSITES(false),
        USERS(false);

        private boolean enabled;
        Properties(boolean defaultEnabled) {
            this.enabled = defaultEnabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return this.enabled;
        }
        public static List<Properties> getAllProperties() {
            Properties[] props = {
                    ID, AVATAR_URL, NAME, PRETTY_NAME, DESCRIPTION, FUNC, EMAIL, TYPE, WEBSITES, USERS
            };
            return new ArrayList<>(Arrays.asList(props));
        }
    }

    private List<Properties> properties;

    public FKITGroupSerializer(List<Properties> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public void selectValues(List<Properties> props) {
        this.properties = new ArrayList<>(props);
    }

    public JSONObject serialize(
            FKITGroup value,
            @Nullable List<JSONObject> groupMembers,
            @Nullable List<EntityWebsiteService.WebsiteView> websites) {
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
        return SerializerUtil.serialize(values, false);

    }
}
