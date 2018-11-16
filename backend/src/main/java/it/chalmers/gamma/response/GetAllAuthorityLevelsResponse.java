package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetAllAuthorityLevelsResponse extends ResponseEntity<List<AuthorityLevel>> {
    public GetAllAuthorityLevelsResponse(List<AuthorityLevel> authorityLevels){
        super(authorityLevels, HttpStatus.OK);
    }
}
