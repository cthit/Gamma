package it.chalmers.gamma.requests;

import java.util.Objects;

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
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUserAgreement() {
        return this.userAgreement;
    }

    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    public int getAcceptanceYear() {
        return this.acceptanceYear;
    }

    public void setAcceptanceYear(int acceptanceYear) {
        this.acceptanceYear = acceptanceYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminViewCreateITUserRequest that = (AdminViewCreateITUserRequest) o;
        return this.userAgreement == that.userAgreement
            && this.acceptanceYear == that.acceptanceYear
            && this.cid.equals(that.cid)
            && this.password.equals(that.password)
            && this.nick.equals(that.nick)
            && this.firstName.equals(that.firstName)
            && this.lastName.equals(that.lastName)
            && this.email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cid, this.password, this.nick, this.firstName,
            this.lastName, this.email, this.userAgreement, this.acceptanceYear);
    }
}
