package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.domain.post.service.PostDTO;

public class AuthorityDTO implements DTO {

    private final SuperGroupDTO superGroup;
    private final PostDTO post;
    private final AuthorityLevelName authorityLevelName;

    public AuthorityDTO(SuperGroupDTO superGroup, PostDTO post, AuthorityLevelName authorityLevelName) {
        this.superGroup = superGroup;
        this.post = post;
        this.authorityLevelName = authorityLevelName;
    }

    public SuperGroupDTO getSuperGroup() {
        return superGroup;
    }

    public PostDTO getPost() {
        return post;
    }

    public AuthorityLevelName getAuthorityLevelName() {
        return authorityLevelName;
    }
}
