package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateGroupRequest {

    @NotNull(message = "NAME_MUST_BE_PROVIDED")
    @Size(max = 50, message = "NAME_TOO_LONG")
    private String name;

    @Size(max = 50, message = "PRETTY_NAME_TOO_LONG")
    private String prettyName;

    private Text description;

    @NotNull(message = "A_FUNCTION_MUST_BE_PROVIDED")
    private Text function;
    private String avatarURL;
    private List<WebsiteInfo> websites;

    @NotNull(message = "BECOMES_ACTIVE_MUST_BE_PROVIDED")       // MORE SPECIFIC CHECK
    private Calendar becomesActive;

    @NotNull(message = "BECOMES_INACTIVE_MUST_BE_PROVIDED")       // MORE SPECIFIC CHECK
    private Calendar becomesInactive;

    private String superGroup;

    private String email;


    public List<WebsiteInfo> getWebsites() {
        return this.websites;
    }

    public String getSuperGroup() {
        return this.superGroup;
    }

    public void setSuperGroup(String superGroup) {
        this.superGroup = superGroup;
    }

    public void setWebsites(List<WebsiteInfo> websites) {
        this.websites = websites;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getDescription() {
        return this.description;
    }

    public Text getFunction() {
        return this.function;
    }

    public void setFunction(Text function) {
        this.function = function;
    }

    public String getAvatarURL() {
        return this.avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public Calendar getBecomesActive() {
        return this.becomesActive;
    }

    public void setBecomesActive(Calendar becomesActive) {
        this.becomesActive = becomesActive;
    }

    public Calendar getBecomesInactive() {
        return this.becomesInactive;
    }

    public void setBecomesInactive(Calendar becomesInactive) {
        this.becomesInactive = becomesInactive;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateGroupRequest that = (CreateGroupRequest) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.prettyName, that.prettyName)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.function, that.function)
                && Objects.equals(this.avatarURL, that.avatarURL)
                && Objects.equals(this.websites, that.websites)
                && Objects.equals(this.becomesActive, that.becomesActive)
                && Objects.equals(this.becomesInactive, that.becomesInactive)
                && Objects.equals(this.superGroup, that.superGroup)
                && Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.prettyName,
                this.description,
                this.function,
                this.avatarURL,
                this.websites,
                this.becomesActive,
                this.becomesInactive,
                this.superGroup,
                this.email);
    }

    @Override
    public String toString() {
        return "CreateGroupRequest{"
                + "name='" + name + '\''
                + ", prettyName='" + prettyName + '\''
                + ", description=" + description
                + ", function=" + function
                + ", avatarURL='" + avatarURL + '\''
                + ", websites=" + websites
                + ", becomesActive=" + becomesActive
                + ", becomesInactive=" + becomesInactive
                + ", superGroup='" + superGroup + '\''
                + ", email='" + email + '\''
                + '}';
    }

    public static class WebsiteInfo {
        String website;
        String url;

        public String getWebsite() {
            return this.website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WebsiteInfo that = (WebsiteInfo) o;
            return this.website.equals(that.website)
                    && this.url.equals(that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.website, this.url);
        }

        @Override
        public String toString() {
            return "WebsiteInfo{"
                    + "website='" + this.website + '\''
                    + ", url='" + this.url + '\''
                    + '}';
        }
    }
}
