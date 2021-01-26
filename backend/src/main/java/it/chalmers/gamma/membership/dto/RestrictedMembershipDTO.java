package it.chalmers.gamma.membership.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserRestrictedDTO;

import java.util.Objects;

public class RestrictedMembershipDTO {

    private final PostDTO post;
    private final GroupDTO groupDTO;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final UserRestrictedDTO user;

    public RestrictedMembershipDTO(MembershipDTO membership) {
        this.post = membership.getPost();
        this.groupDTO = membership.getFkitGroupDTO();
        this.unofficialPostName = membership.getUnofficialPostName();
        this.user = new UserRestrictedDTO(membership.getUser());
    }

    public PostDTO getPost() {
        return this.post;
    }

    public GroupDTO getFkitGroupDTO() {
        return this.groupDTO;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public UserRestrictedDTO getUser() {
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
            && Objects.equals(this.groupDTO, that.groupDTO)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName)
            && Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.groupDTO, this.unofficialPostName, this.user);
    }

    @Override
    public String toString() {
        return "RestrictedMembershipDTO{"
            + "post=" + this.post
            + ", fkitGroupDTO=" + this.groupDTO
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + ", user=" + this.user
            + '}';
    }
}
