package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.domain.GroupType;

import java.util.List;

public class CreateGroupRequest {
    private String name;
    private String prettyName;
    private Text description;
    private String email;
    private GroupType type;
    private Text func;
    private String avatarURL;
    private List<WebsiteInfo> websites;

    public List<WebsiteInfo> getWebsites() {
        return websites;
    }

    public void setWebsites(List<WebsiteInfo> websites) {
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

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public static class WebsiteInfo{
        String website;
        String url;

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
