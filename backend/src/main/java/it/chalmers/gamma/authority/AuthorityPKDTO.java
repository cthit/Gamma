package it.chalmers.gamma.authority;

import it.chalmers.gamma.supergroup.FKITSuperGroupDTO;
import it.chalmers.gamma.post.PostDTO;

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
