package it.chalmers.gamma.db.serializers;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.util.SerializerUtils;
import it.chalmers.gamma.views.WebsiteView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.json.simple.JSONObject;

public class ITUserSerializer {

    private final List<Properties> properties;

    public ITUserSerializer(List<Properties> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public static List<JSONObject> getGroupsAsJson(List<Membership> memberships) {
        FKITGroupSerializer groupSerializer = new FKITGroupSerializer(Arrays.asList(
                FKITGroupSerializer.Properties.ID,
                FKITGroupSerializer.Properties.NAME,
                FKITGroupSerializer.Properties.PRETTY_NAME,
                FKITGroupSerializer.Properties.SUPER_GROUP));
        List<JSONObject> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            groups.add(groupSerializer.serialize(membership.getId().getFKITGroup(), null, null));
        }
        return groups;
    }

    public JSONObject serialize(ITUser value,
                                @Nullable List<WebsiteView> websites,
                                @Nullable List<JSONObject> groups) {
        List<SerializerValue> values = new ArrayList<>();
        values.add(serializeValue(Properties.ID, value.getId(), "id"));
        values.add(serializeValue(Properties.AVATAR_URL, value.getAvatarUrl(), "avatarUrl"));
        values.add(serializeValue(Properties.CID, value.getCid(), "cid"));
        values.add(serializeValue(Properties.NICK, value.getNick(), "nickname"));
        values.add(serializeValue(Properties.FIRST_NAME, value.getFirstName(), "firstName"));
        values.add(serializeValue(Properties.LAST_NAME, value.getLastName(), "lastName"));
        values.add(serializeValue(Properties.EMAIL, value.getEmail(), "email"));
        values.add(serializeValue(Properties.PHONE, value.getPhone(), "phone"));
        values.add(serializeValue(Properties.LANGUAGE, value.getLanguage(), "language"));
        values.add(serializeValue(Properties.USER_AGREEMENT, value.isUserAgreement(), "userAgreementAccepted"));
        values.add(serializeValue(Properties.ACCOUNT_LOCKED, value.isAccountLocked(), "accountLocked"));
        values.add(serializeValue(Properties.ACCEPTANCE_YEAR, value.getAcceptanceYear(), "acceptanceYear"));
        values.add(serializeValue(Properties.CREATED_AT, value.getCreatedAt(), "createdAt"));
        values.add(serializeValue(Properties.LAST_MODIFIED_AT, value.getLastModifiedAt(), "lastModifiedAt"));
        values.add(serializeValue(Properties.WEBSITE, websites, "websites"));
        values.add(serializeValue(Properties.AUTHORITIES, value.getAuthorities(), "authorities"));
        values.add(serializeValue(Properties.GROUPS, groups, "groups"));
        values.add(serializeValue(Properties.GDPR, value.isGdpr(), "gdpr"));
        return SerializerUtils.serialize(values, false);
    }

    private SerializerValue serializeValue(Properties properties, Object value, String name) {
        return new SerializerValue(this.properties.contains(properties), value, name);
    }

    public enum Properties {
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
        AUTHORITIES,
        WEBSITE,
        GROUPS,
        GDPR;

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
                AUTHORITIES,
                WEBSITE,
                GROUPS,
                GDPR

            };
            return new ArrayList<>(Arrays.asList(props));
        }
    }

}
