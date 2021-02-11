package it.chalmers.gamma.domain.whitelist.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class SomeAddedToWhitelistResponse {

    private List<String> failedToAdd;

    public SomeAddedToWhitelistResponse(List<String> failedToAdd) {
        this.failedToAdd = failedToAdd;
    }

    public List<String> getFailedToAdd() {
        return failedToAdd;
    }

    public SomeAddedToWhitelistResponseObject toResponseObject() {
        return new SomeAddedToWhitelistResponseObject(this);
    }

    public static class SomeAddedToWhitelistResponseObject extends ResponseEntity<SomeAddedToWhitelistResponse> {

        public SomeAddedToWhitelistResponseObject(SomeAddedToWhitelistResponse body) {
            super(body, HttpStatus.PARTIAL_CONTENT);
        }
    }

}
