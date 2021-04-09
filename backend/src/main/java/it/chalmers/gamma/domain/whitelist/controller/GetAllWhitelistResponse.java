package it.chalmers.gamma.domain.whitelist.controller;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.Cid;

public class GetAllWhitelistResponse{

    @JsonValue
    private final List<Cid> whitelist;

    public GetAllWhitelistResponse(List<Cid> whitelist) {
        this.whitelist = whitelist;
    }

}
