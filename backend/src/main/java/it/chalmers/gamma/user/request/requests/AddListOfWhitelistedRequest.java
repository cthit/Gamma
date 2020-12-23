package it.chalmers.gamma.user.request.requests;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AddListOfWhitelistedRequest {
    @NotEmpty(message = "NO_CID_IN_REQUEST")
    private List<@Size(min = 4, max = 12, message = "CIDS_MUST_BE_BETWEEN_4_AND_12_CHARACTERS") String> cids;

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
