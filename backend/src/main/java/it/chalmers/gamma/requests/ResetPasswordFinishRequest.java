package it.chalmers.delta.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResetPasswordFinishRequest {
    @Size(min = 8, message = "NEW_PASSWORD_TOO_SHORT")
    @NotNull(message = "NEW_PASSWORD_MUST_BE_PROVIDED")
    private String password;
    @NotEmpty(message = "CID_MUST_BE_PROVIDED")
    private String cid;
    @NotEmpty(message = "A_TOKEN_MUST_BE_PROVIDED")
    private String token;

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
