package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.Language;

import java.util.Objects;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdminViewCreateUserRequest {

    @NotEmpty(message = "CID_MUST_BE_PROVIDED")
    @Size(min = 4, max = 12, message = "CIDS_MUST_BE_BETWEEN_4_AND_12_CHARACTERS")
    private String cid;

    @Size(min = 8, message = "PASSWORD_MUST_BE_MORE_THAN_8_CHARACTERS")
    private String password;

    @NotEmpty(message = "NICK_MUST_BE_PROVIDED")
    private String nick;

    @NotEmpty(message = "FIRST_NAME_MUST_BE_PROVIDED")
    private String firstName;

    @NotEmpty(message = "LAST_NAME_MUST_BE_PROVIDED")
    private String lastName;

    @NotEmpty(message = "EMAIL_NAME_MUST_BE_PROVIDED")
    @Email(message = "NOT_A_VALID_EMAIL")
    private String email;

    @AssertTrue(message = "USER_AGREEMENT_MUST_BE_ACCEPTED")
    private boolean userAgreement;

    @Min(value = 2001, message = "ACCEPTANCE_YEAR_MUST_BE_AFTER_2001")
    private int acceptanceYear;

    private Language language = Language.SV;

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

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminViewCreateUserRequest that = (AdminViewCreateUserRequest) o;
        return this.userAgreement == that.userAgreement
                && this.acceptanceYear == that.acceptanceYear
                && Objects.equals(this.cid, that.cid)
                && Objects.equals(this.password, that.password)
                && Objects.equals(this.nick, that.nick)
                && Objects.equals(this.firstName, that.firstName)
                && Objects.equals(this.lastName, that.lastName)
                && Objects.equals(this.email, that.email)
                && this.language == that.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cid,
                this.password,
                this.nick,
                this.firstName,
                this.lastName,
                this.email,
                this.userAgreement,
                this.acceptanceYear,
                this.language);
    }
}
