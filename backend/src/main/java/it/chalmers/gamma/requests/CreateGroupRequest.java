package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.domain.GroupType;

import java.util.List;

public class CreateGroupRequest {
    private String name;
    private Text description;
    private String email;
    private GroupType type;
    private Text func;
    private String avatarURL;
    private List<WebsiteURL> websites;

    public List<WebsiteURL> getWebsites() {
        return websites;
    }

    public void setWebsites(List<WebsiteURL> websites) {
        this.websites = websites;
    }


    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getDescription() {
        return description;
    }

    public Text getFunc() {
        return func;
    }

    public void setFunc(Text func) {
        this.func = func;
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

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }
}
