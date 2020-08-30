package it.chalmers.gamma.domain.dto.membership;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserRestrictedDTO;

import java.util.Objects;

public class RestrictedMembershipDTO {

    private final PostDTO post;
    private final FKITGroupDTO fkitGroupDTO;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final ITUserRestrictedDTO user;

    public RestrictedMembershipDTO(MembershipDTO membership) {
        this.post = membership.getPost();
        this.fkitGroupDTO = membership.getFkitGroupDTO();
        this.unofficialPostName = membership.getUnofficialPostName();
        this.user = new ITUserRestrictedDTO(membership.getUser());
    }

    public PostDTO getPost() {
        return this.post;
    }

    public FKITGroupDTO getFkitGroupDTO() {
        return this.fkitGroupDTO;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public ITUserRestrictedDTO getUser() {
        return this.user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestrictedMembershipDTO that = (RestrictedMembershipDTO) o;
        return Objects.equals(this.post, that.post)
            && Objects.equals(this.fkitGroupDTO, that.fkitGroupDTO)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName)
            && Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.fkitGroupDTO, this.unofficialPostName, this.user);
    }

    @Override
    public String toString() {
        return "RestrictedMembershipDTO{"
            + "post=" + this.post
            + ", fkitGroupDTO=" + this.fkitGroupDTO
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + ", user=" + this.user
            + '}';
    }
}
