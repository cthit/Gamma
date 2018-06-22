package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Whitelist;

import java.util.Objects;

public class CreateITUserRequest {

    private String code;
    private String password;
    private String nick;
    private String firstName;
    private String lastName;
    private boolean userAgreement;
    private int acceptanceYear;
    private Whitelist cid;

    public Whitelist getCid() {
        return cid;
    }

    public void setCid(Whitelist cid) {
        this.cid = cid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "CreateITUserRequest{" +
                "code='" + code + '\'' +
                ", password='" + password + '\'' +
                ", nick='" + nick + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userAgreement=" + userAgreement +
                ", acceptanceYear=" + acceptanceYear +
                ", cid=" + cid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateITUserRequest that = (CreateITUserRequest) o;
        return userAgreement == that.userAgreement &&
                acceptanceYear == that.acceptanceYear &&
                Objects.equals(code, that.code) &&
                Objects.equals(password, that.password) &&
                Objects.equals(nick, that.nick) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, password, nick, firstName, lastName, userAgreement, acceptanceYear, cid);
    }
}
