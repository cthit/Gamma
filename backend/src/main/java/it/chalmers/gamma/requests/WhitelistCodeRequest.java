package it.chalmers.gamma.requests;

import java.util.UUID;

public class WhitelistCodeRequest {
    private String cid;
    private UUID id;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
