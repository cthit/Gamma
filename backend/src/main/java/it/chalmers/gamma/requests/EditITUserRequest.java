package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.Language;

import java.util.List;
import java.util.Objects;

public class EditITUserRequest {
    private String nick;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Language language;
    private String avatarUrl;
    private List<CreateGroupRequest.WebsiteInfo> websites;

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<CreateGroupRequest.WebsiteInfo> getWebsites() {
        return this.websites;
    }

    public void setWebsites(List<CreateGroupRequest.WebsiteInfo> websites) {
        this.websites = websites;
    }

    public String getNick() {
        return this.nick;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Language getLanguage() {
        return this.language;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EditITUserRequest that = (EditITUserRequest) o;
        return this.nick.equals(that.nick)
            && this.firstName.equals(that.firstName)
            && this.lastName.equals(that.lastName)
            && this.email.equals(that.email)
            && this.phone.equals(that.phone)
            && this.language == that.language
            && this.avatarUrl.equals(that.avatarUrl)
            && this.websites.equals(that.websites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nick, this.firstName, this.lastName, this.email,
            this.phone, this.language, this.avatarUrl, this.websites);
    }

    @Override
    public String toString() {
        return "EditITUserRequest{"
            + "nick='" + this.nick + '\''
            + ", firstName='" + this.firstName + '\''
            + ", lastName='" + this.lastName + '\''
            + ", email='" + this.email + '\''
            + ", phone='" + this.phone + '\''
            + ", language=" + this.language
            + ", avatarUrl='" + this.avatarUrl + '\''
            + ", websites=" + this.websites
            + '}';
    }
}
