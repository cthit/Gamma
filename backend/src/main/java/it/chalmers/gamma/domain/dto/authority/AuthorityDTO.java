package it.chalmers.gamma.domain.dto.authority;

import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import java.util.Objects;
import java.util.UUID;

public class AuthorityDTO {
    private final FKITSuperGroupDTO superGroup;
    private final PostDTO post;
    private final UUID internalID;
    private final AuthorityLevelDTO authorityLevel;

    public AuthorityDTO(FKITSuperGroupDTO superGroup, PostDTO post,
                        UUID internalID, AuthorityLevelDTO authorityLevel) {
        this.superGroup = superGroup;
        this.post = post;
        this.internalID = internalID;
        this.authorityLevel = authorityLevel;
    }

    public FKITSuperGroupDTO getSuperGroup() {
        return this.superGroup;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public UUID getInternalID() {
        return this.internalID;
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
                && Objects.equals(this.internalID, that.internalID)
                && Objects.equals(this.authorityLevel, that.authorityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.superGroup, this.post, this.internalID, this.authorityLevel);
    }

    @Override
    public String toString() {
        return "AuthorityDTO{"
                + "superGroup=" + this.superGroup
                + ", post=" + this.post
                + ", internalID=" + this.internalID
                + ", authorityLevel=" + this.authorityLevel
                + '}';
    }
}
