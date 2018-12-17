package it.chalmers.gamma.requests;

import java.util.Objects;
import javax.validation.constraints.*;

public class AdminViewCreateITUserRequest {
    @NotEmpty(message = "a cid must be supplied")
    @Size(min = 4, max = 12, message = "cid must be between 4 and 12 characters")
    private String cid;
    @Min(value = 8, message = "password must be at least 8 characters long")
    @NotEmpty(message = "a password must be supplied")
    private String password;
    @NotEmpty(message = "a cid must be supplied")
    private String nick;
    @NotEmpty(message = "a first name must be supplied")
    private String firstName;
    @NotEmpty(message = "a last name must be supplied")
    private String lastName;
    @NotEmpty(message = "an email must be supplied")
    @Email(message = "email must be valid")
    private String email;
    @AssertTrue(message = "the user agreement must be accepted before an account can be created")
    private boolean userAgreement;
    @Min(value = 2001, message = "acceptance year cannot be before 2001")
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
