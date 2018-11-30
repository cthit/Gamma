package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.service.WebsiteView;
import it.chalmers.gamma.util.SerializerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;

public class ITUserSerializer {

    public enum Properties {
        ID(false),
        AVATAR_URL(false),
        CID(false),
        NICK(false),
        FIRST_NAME(false),
        LAST_NAME(false),
        EMAIL(false),
        PHONE(false),
        LANGUAGE(false),
        USER_AGREEMENT(false),
        ACCOUNT_LOCKED(false),
        ACCEPTANCE_YEAR(false),
        CREATED_AT(false),
        LAST_MODIFIED_AT(false),
        WEBSITE(false);

        private boolean enabled;

        Properties(boolean defaultEnabled) {
            this.enabled = defaultEnabled;
        }

        public static List<ITUserSerializer.Properties> getAllProperties() {
            ITUserSerializer.Properties[] props = {
                ID,
                AVATAR_URL,
                CID,
                NICK,
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                LANGUAGE,
                USER_AGREEMENT,
                ACCOUNT_LOCKED,
                ACCEPTANCE_YEAR,
                CREATED_AT,
                LAST_MODIFIED_AT,
                WEBSITE
            };
            return new ArrayList<>(Arrays.asList(props));
        }
    }

    private List<Properties> properties;

    public ITUserSerializer(List<Properties> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public JSONObject serialize(ITUser value, List<WebsiteView> websites) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(serializeValue(Properties.ID, value.getId(), "id"));
        values.add(serializeValue(Properties.AVATAR_URL, value.getAvatarUrl(), "avatar_url"));
        values.add(serializeValue(Properties.CID, value.getCid(), "cid"));
        values.add(serializeValue(Properties.NICK, value.getNick(), "nickname"));
        values.add(serializeValue(Properties.FIRST_NAME, value.getFirstName(), "first_name"));
        values.add(serializeValue(Properties.LAST_NAME, value.getLastName(), "last_name"));
        values.add(serializeValue(Properties.EMAIL, value.getEmail(), "email"));
        values.add(serializeValue(Properties.PHONE, value.getPhone(), "phone"));
        values.add(serializeValue(Properties.LANGUAGE, value.getLanguage(), "language"));
        values.add(serializeValue(Properties.USER_AGREEMENT, value.isUserAgreement(), "user_agreement_accepted"));
        values.add(serializeValue(Properties.ACCOUNT_LOCKED, value.isAccountLocked(), "account_locked"));
        values.add(serializeValue(Properties.ACCEPTANCE_YEAR, value.getAcceptanceYear(), "acceptance_year"));
        values.add(serializeValue(Properties.CREATED_AT, value.getCreatedAt(), "created_at"));
        values.add(serializeValue(Properties.LAST_MODIFIED_AT, value.getLastModifiedAt(), "last_modified_at"));
        values.add(serializeValue(Properties.WEBSITE, websites, "websites"));

        return SerializerUtils.serialize(values, false);
    }

    private SerializerValue serializeValue(Properties properties, Object value, String name) {
        return new SerializerValue(this.properties.contains(properties), value, name);
    }

}
