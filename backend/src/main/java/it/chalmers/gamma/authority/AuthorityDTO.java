package it.chalmers.gamma.authority;

import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.post.PostDTO;
import java.util.Objects;
import java.util.UUID;

public class AuthorityDTO {
    private final SuperGroupDTO superGroup;
    private final PostDTO post;
    private final UUID id;
    private final AuthorityLevelDTO authorityLevel;

    public AuthorityDTO(SuperGroupDTO superGroup, PostDTO post,
                        UUID internalID, AuthorityLevelDTO authorityLevel) {
        this.superGroup = superGroup;
        this.post = post;
        this.id = internalID;
        this.authorityLevel = authorityLevel;
    }

    public SuperGroupDTO getSuperGroup() {
        return this.superGroup;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public UUID getId() {
        return this.id;
    }

    public AuthorityLevelDTO getAuthorityLevel() {
        return this.authorityLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorityDTO that = (AuthorityDTO) o;
        return Objects.equals(this.superGroup, that.superGroup)
                && Objects.equals(this.post, that.post)
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.authorityLevel, that.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.superGroup, this.post, this.id, this.authorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorityDTO{"
                + "superGroup=" + this.superGroup
                + ", post=" + this.post
                + ", internalID=" + this.id
                + ", authorityLevel=" + this.authorityLevel
                + '}';
    }
}
