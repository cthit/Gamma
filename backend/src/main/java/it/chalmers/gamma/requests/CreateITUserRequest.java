package it.chalmers.gamma.requests;

import java.util.Objects;

public class CreateITUserRequest {

    private String password;
    private String nick;
    private String firstName;
    private String lastName;
    private boolean userAgreement;
    private int acceptanceYear;


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
                "password='" + "<redacted>" + '\'' +
                ", nick='" + nick + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userAgreement=" + userAgreement +
                ", acceptanceYear=" + acceptanceYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateITUserRequest that = (CreateITUserRequest) o;
        return userAgreement == that.userAgreement &&
                acceptanceYear == that.acceptanceYear &&
                Objects.equals(password, that.password) &&
                Objects.equals(nick, that.nick) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password, nick, firstName, lastName, userAgreement, acceptanceYear);
    }
}
