package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;

public class CreateGroupRequest {
    private String name;
    private Text description;
    private String email;
    private GroupType groupType;
    private Text function;
    private String avatarURL;

    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getDescription() {
        return description;
    }

    public Text getFunction() {
        return function;
    }

    public void setFunction(Text function) {
        this.function = function;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
}
