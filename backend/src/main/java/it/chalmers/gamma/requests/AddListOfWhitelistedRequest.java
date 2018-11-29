package it.chalmers.gamma.requests;

import java.util.List;
import java.util.Objects;

public class AddListOfWhitelistedRequest {
    private List<String> cids;

    public List<String> getCids() {
        return this.cids;
    }

    public void setCids(List<String> cid) {
        this.cids = cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddListOfWhitelistedRequest that = (AddListOfWhitelistedRequest) o;
        return this.cids.equals(that.cids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cids);
    }

    @Override
    public String toString() {
        return "AddListOfWhitelistedRequest{"
            + "cids=" + this.cids
            + '}';
    }
}
