package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.domain.activationcode.service.Code;
import it.chalmers.gamma.util.domain.Language;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserRequest {

    private Code code;

    @NotEmpty(message = "PASSWORD_MUST_BE_PROVIDED")
    @Size(min = 8, message = "PASSWORD_MUST_BE_MORE_THAN_8_CHARACTERS")
    private String password;

    @NotEmpty(message = "NICK_MUST_BE_PROVIDED")
    private String nick;

    @NotEmpty(message = "FIRST_NAME_MUST_BE_PROVIDED")
    private String firstName;

    @NotEmpty(message = "EMAIL_REQUIRED")
    @Email(message = "NON_EMAIL_ENTERED")
    @Pattern(regexp = "^((?!@student.chalmers.se).)*$", message = "STUDENT_MAIL_ENTERED")
    private String email;

    @NotEmpty(message = "LAST_NAME_MUST_BE_PROVIDED")
    private String lastName;

    @AssertTrue(message = "USER_AGREEMENT_MUST_BE_ACCEPTED")
    private boolean userAgreement;

    @Min(value = 2001, message = "ACCEPTANCE_YEAR_MUST_BE_AFTER_2001")
    private int acceptanceYear;

    @NotNull(message = "WHITELIST_MUST_BE_PROVIDED")
    private String cid;

    private Language language = Language.SV;

    public Code getCode() {
        return this.code;
    }

    public void setCode(Code code) {
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

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
