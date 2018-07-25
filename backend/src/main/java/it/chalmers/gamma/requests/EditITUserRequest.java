package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.Language;

public class EditITUserRequest {
    String nick;
    String firstName;
    String lastName;
    String email;
    String phone;
    Language language;
    String avatarUrl;

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
