package it.chalmers.gamma.domain.dto.authority;

import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import java.util.Objects;
import java.util.UUID;

public class AuthorityDTO {
    private final FKITSuperGroupDTO fkitSuperGroup;
    private final PostDTO post;
    private final UUID internalID;
    private final AuthorityLevelDTO authorityLevelDTO;

    public AuthorityDTO(FKITSuperGroupDTO fkitSuperGroup, PostDTO post,
                        UUID internalID, AuthorityLevelDTO authorityLevelDTO) {
        this.fkitSuperGroup = fkitSuperGroup;
        this.post = post;
        this.internalID = internalID;
        this.authorityLevelDTO = authorityLevelDTO;
    }

    public FKITSuperGroupDTO getFkitSuperGroup() {
        return this.fkitSuperGroup;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public UUID getInternalID() {
        return this.internalID;
    }

    public AuthorityLevelDTO getAuthorityLevelDTO() {
        return this.authorityLevelDTO;
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
        return Objects.equals(this.fkitSuperGroup, that.fkitSuperGroup)
                && Objects.equals(this.post, that.post)
                && Objects.equals(this.internalID, that.internalID)
                && Objects.equals(this.authorityLevelDTO, that.authorityLevelDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fkitSuperGroup, this.post, this.internalID, this.authorityLevelDTO);
    }

    @Override
    public String toString() {
        return "AuthorityDTO{"
                + "fkitSuperGroup=" + this.fkitSuperGroup
                + ", post=" + this.post
                + ", internalID=" + this.internalID
                + ", authorityLevelDTO=" + this.authorityLevelDTO
                + '}';
    }
}
