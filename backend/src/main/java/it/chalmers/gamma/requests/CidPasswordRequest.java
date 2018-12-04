package it.chalmers.gamma.requests;

import java.util.Objects;

public class CidPasswordRequest {

    private String cid;
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
