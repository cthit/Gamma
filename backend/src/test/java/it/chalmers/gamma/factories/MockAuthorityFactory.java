package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.authority.AuthorityDTO;
import it.chalmers.gamma.domain.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.post.PostDTO;
import it.chalmers.gamma.authority.AddAuthorityRequest;
import it.chalmers.gamma.authority.AuthorityService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockAuthorityFactory {

    @Autowired
    private AuthorityService authorityService;

    public AuthorityDTO generateAuthority(FKITSuperGroupDTO superGroup,
                                          PostDTO post,
                                          AuthorityLevelDTO authorityLevel) {
        return new AuthorityDTO(
                superGroup,
                post,
                UUID.randomUUID(),
                authorityLevel
        );
    }

    public AuthorityDTO saveAuthority(AuthorityDTO authority) {
        return this.authorityService.createAuthority(
                authority.getSuperGroup(),
                authority.getPost(),
                authority.getAuthorityLevel());
    }

    public AddAuthorityRequest createValidRequest(AuthorityDTO authority, PostDTO post, FKITSuperGroupDTO superGroup) {
        AddAuthorityRequest request = new AddAuthorityRequest();
        request.setAuthority(authority.getAuthorityLevel().getId().toString());
        request.setPost(post.getId().toString());
        request.setSuperGroup(superGroup.getId().toString());
        return request;
    }
}
