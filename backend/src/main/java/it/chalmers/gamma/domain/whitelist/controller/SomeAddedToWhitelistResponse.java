package it.chalmers.gamma.domain.whitelist.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.Cid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class SomeAddedToWhitelistResponse {

    @JsonValue
    public final List<Cid> failedToAdd;

    public SomeAddedToWhitelistResponse(List<Cid> failedToAdd) {
        this.failedToAdd = failedToAdd;
    }

}
