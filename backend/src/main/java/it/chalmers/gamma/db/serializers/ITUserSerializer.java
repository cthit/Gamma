package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.service.EntityWebsiteService;
import it.chalmers.gamma.util.SerializerUtil;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ITUserSerializer {

    public enum Properties{
        ID(false), AVATAR_URL(false), CID(false), NICK(false), FIRST_NAME(false), LAST_NAME(false), EMAIL(false),
        PHONE(false), LANGUAGE(false), USER_AGREEMENT(false), ACCOUNT_LOCKED(false), ACCEPTANCE_YEAR(false),
        CREATED_AT(false), LAST_MODIFIED_AT(false), WEBSITE(false);
        private boolean enabled;
        Properties(boolean defaultEnabled){
            enabled = defaultEnabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
        public static List<ITUserSerializer.Properties> getAllProperties(){
            ITUserSerializer.Properties[] props = {ID, AVATAR_URL, CID, NICK, FIRST_NAME, LAST_NAME, EMAIL,
                    PHONE, LANGUAGE, USER_AGREEMENT, ACCOUNT_LOCKED, ACCEPTANCE_YEAR, CREATED_AT, LAST_MODIFIED_AT, WEBSITE};
            return new ArrayList<>(Arrays.asList(props));
        }
    }
    private List<Properties> properties;

    public ITUserSerializer(List<Properties> properties){
        this.properties = new ArrayList<>(properties);
    }
    public JSONObject serialize(ITUser value, List<EntityWebsiteService.WebsiteView> websites){
        List<SerializerValue> values = new ArrayList<>();
        values.add(new SerializerValue(properties.contains(Properties.ID), value.getId(), "id"));
        values.add(new SerializerValue(properties.contains(Properties.AVATAR_URL), value.getAvatarUrl(), "avatar_url"));
        values.add(new SerializerValue(properties.contains(Properties.CID), value.getCid(), "cid"));
        values.add(new SerializerValue(properties.contains(Properties.NICK), value.getNick(), "nickname"));
        values.add(new SerializerValue(properties.contains(Properties.FIRST_NAME), value.getFirstName(), "first_name"));
        values.add(new SerializerValue(properties.contains(Properties.LAST_NAME), value.getLastName(), "last_name"));
        values.add(new SerializerValue(properties.contains(Properties.EMAIL), value.getEmail(), "email"));
        values.add(new SerializerValue(properties.contains(Properties.PHONE), value.getPhone(), "phone"));
        values.add(new SerializerValue(properties.contains(Properties.LANGUAGE), value.getLanguage(), "language"));
        values.add(new SerializerValue(properties.contains(Properties.USER_AGREEMENT), value.isUserAgreement(), "user_agreement_accepted"));
        values.add(new SerializerValue(properties.contains(Properties.ACCOUNT_LOCKED), value.isAccountLocked(), "account_locked"));
        values.add(new SerializerValue(properties.contains(Properties.ACCEPTANCE_YEAR), value.getAcceptanceYear(), "acceptance_year"));
        values.add(new SerializerValue(properties.contains(Properties.CREATED_AT), value.getCreatedAt(), "created_at"));
        values.add(new SerializerValue(properties.contains(Properties.LAST_MODIFIED_AT), value.getLastModifiedAt(), "last_modified_at"));
        values.add(new SerializerValue(properties.contains(Properties.WEBSITE), websites, "websites"));
        return SerializerUtil.serialize(values, false);
    }

}
