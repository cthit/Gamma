package it.chalmers.gamma.authority.response;

import it.chalmers.gamma.authority.AuthorityDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAuthorityResponse {
    private final AuthorityDTO authority;


    public GetAuthorityResponse(AuthorityDTO authority) {
        this.authority = authority;
    }

    public AuthorityDTO getAuthority() {
        return this.authority;
    }

    public GetAuthorityResponseObject toResponseObject() {
        return new GetAuthorityResponseObject(this);
    }

    public static class GetAuthorityResponseObject extends ResponseEntity<GetAuthorityResponse> {
        GetAuthorityResponseObject(GetAuthorityResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
