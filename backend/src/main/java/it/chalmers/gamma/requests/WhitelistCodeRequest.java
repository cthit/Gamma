package it.chalmers.gamma.requests;

import java.util.Objects;

public class WhitelistCodeRequest {
    private String cid;

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WhitelistCodeRequest that = (WhitelistCodeRequest) o;
        return this.cid.equals(that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cid);
    }

    @Override
    public String toString() {
        return "WhitelistCodeRequest{"
            + "cid='" + this.cid + '\''
            + '}';
    }
}
