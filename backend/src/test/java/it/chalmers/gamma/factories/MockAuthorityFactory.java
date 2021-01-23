package it.chalmers.gamma.factories;

import it.chalmers.gamma.authority.AuthorityDTO;
import it.chalmers.gamma.authoritylevel.AuthorityLevelDTO;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.authority.request.AddAuthorityRequest;
import it.chalmers.gamma.authority.AuthorityService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockAuthorityFactory {

    @Autowired
    private AuthorityService authorityService;

    public AuthorityDTO generateAuthority(SuperGroupDTO superGroup,
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

    public AddAuthorityRequest createValidRequest(AuthorityDTO authority, PostDTO post, SuperGroupDTO superGroup) {
        AddAuthorityRequest request = new AddAuthorityRequest();
        request.setAuthority(authority.getAuthorityLevel().getId().toString());
        request.setPost(post.getId().toString());
        request.setSuperGroup(superGroup.getId().toString());
        return request;
    }
}
