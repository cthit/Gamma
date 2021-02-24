package it.chalmers.gamma.domain.authority.data.dto;

import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;

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
