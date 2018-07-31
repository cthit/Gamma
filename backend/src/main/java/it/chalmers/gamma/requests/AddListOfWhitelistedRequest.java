package it.chalmers.gamma.requests;

import java.util.List;

public class AddListOfWhitelistedRequest {
    private List<String> cids;

    public List<String> getCids() {
        return cids;
    }

    public void setCids(List<String> cid) {
        this.cids = cid;
    }
}
