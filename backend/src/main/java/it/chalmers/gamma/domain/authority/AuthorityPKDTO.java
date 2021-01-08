package it.chalmers.gamma.domain.authority;

import it.chalmers.gamma.domain.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.post.PostDTO;

public class AuthorityPKDTO {

    private final FKITSuperGroupDTO superGroup;
    private final PostDTO post;

    public AuthorityPKDTO(FKITSuperGroupDTO superGroup, PostDTO post) {
        this.superGroup = superGroup;
        this.post = post;
    }

    public FKITSuperGroupDTO getSuperGroup() {
        return superGroup;
    }

    public PostDTO getPost() {
        return post;
    }
}
