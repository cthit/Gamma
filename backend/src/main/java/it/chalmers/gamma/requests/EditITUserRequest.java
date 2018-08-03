package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.Language;

import java.util.List;

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
        return websites;
    }

    public void setWebsites(List<CreateGroupRequest.WebsiteInfo> websites) {
        this.websites = websites;
    }

    public String getNick() {
        return nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Language getLanguage() {
        return language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
