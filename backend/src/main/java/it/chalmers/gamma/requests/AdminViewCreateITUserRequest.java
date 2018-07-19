package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Whitelist;

public class AdminViewCreateITUserRequest {
    private String cid;
    private String password;
    private String nick;
    private String firstName;
    private String lastName;
    private String email;
    private boolean userAgreement;
    private int acceptanceYear;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUserAgreement() {
        return userAgreement;
    }

    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    public int getAcceptanceYear() {
        return acceptanceYear;
    }

    public void setAcceptanceYear(int acceptanceYear) {
        this.acceptanceYear = acceptanceYear;
    }
}
