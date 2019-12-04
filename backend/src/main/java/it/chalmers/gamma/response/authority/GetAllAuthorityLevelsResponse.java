package it.chalmers.gamma.response.authority;


import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllAuthorityLevelsResponse {
    private final List<AuthorityLevelDTO> authorityLevels;

    public GetAllAuthorityLevelsResponse(List<AuthorityLevelDTO> authorityLevels) {
        this.authorityLevels = authorityLevels;
    }

    public List<AuthorityLevelDTO> getAuthorityLevels() {
        return authorityLevels;
    }

    public GetAllAuthorityLevelsResponseObject getResponseObject() {
        return new GetAllAuthorityLevelsResponseObject(this);
    }

    public static class GetAllAuthorityLevelsResponseObject extends ResponseEntity<GetAllAuthorityLevelsResponse> {
        GetAllAuthorityLevelsResponseObject(GetAllAuthorityLevelsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
