package it.chalmers.gamma.requests;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class CidPasswordRequest {
    @NotNull(message = "CID_MUST_BE_PROVIDED")
    private String cid;
    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    private String password;

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

    @Override
    public String toString() {
        return "CidPasswordRequest{"
            + "cid='" + cid + '\''
            + ", password='[REDACTED]'"
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
        CidPasswordRequest that = (CidPasswordRequest) o;
        return Objects.equals(this.cid, that.cid)
            && Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cid, this.password);
    }
}
