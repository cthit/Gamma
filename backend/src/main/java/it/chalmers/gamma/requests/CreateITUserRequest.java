package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Whitelist;

import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CreateITUserRequest {
    @NotEmpty(message = "CODE_MUST_BE_PROVIDED")
    // TODO SPECIFY MINIMUM AND MAXIMUM LENGTH OF CODE
    private String code;
    @Size(min = 8, message = "PASSWORD_MUST_BE_MORE_THAN_8_CHARACTERS")
    private String password;
    @NotEmpty(message = "NICK_MUST_BE_PROVIDED")
    private String nick;
    @NotEmpty(message = "FIRST_NAME_MUST_BE_PROVIDED")
    private String firstName;
    @NotEmpty(message = "LAST_NAME_MUST_BE_PROVIDED")
    private String lastName;
    @NotEmpty(message = "USER_AGREEMENT_MUST_BE_ACCEPTED")
    private boolean userAgreement;
    @Min(value = 2001, message = "ACCEPTANCE_YEAR_MUST_BE_AFTER_2001")
    private int acceptanceYear;
    @NotNull(message = "WHITELIST_MUST_BE_PROVIDED")
    private Whitelist whitelist;

    public Whitelist getWhitelist() {
        return this.whitelist;
    }

    public void setWhitelist(Whitelist cid) {
        this.whitelist = cid;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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
    public String toString() {
        return "CreateITUserRequest{"
            + "code='" + this.code + '\''
            + ", password='" + this.password + '\''
            + ", nick='" + this.nick + '\''
            + ", firstName='" + this.firstName + '\''
            + ", lastName='" + this.lastName + '\''
            + ", userAgreement=" + this.userAgreement
            + ", acceptanceYear=" + this.acceptanceYear
            + ", whitelist=" + this.whitelist
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateITUserRequest that = (CreateITUserRequest) o;
        return this.userAgreement == that.userAgreement
            && this.acceptanceYear == that.acceptanceYear
            && Objects.equals(this.code, that.code)
            && Objects.equals(this.password, that.password)
            && Objects.equals(this.nick, that.nick)
            && Objects.equals(this.firstName, that.firstName)
            && Objects.equals(this.lastName, that.lastName)
            && Objects.equals(this.whitelist, that.whitelist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.password, this.nick, this.firstName,
            this.lastName, this.userAgreement, this.acceptanceYear, this.whitelist);
    }
}
