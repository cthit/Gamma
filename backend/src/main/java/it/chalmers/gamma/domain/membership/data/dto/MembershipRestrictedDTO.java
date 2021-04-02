package it.chalmers.gamma.domain.membership.data.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.group.data.dto.GroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;

import java.util.Objects;

public class MembershipRestrictedDTO {

    private final PostDTO post;
    private final GroupDTO group;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final UserRestrictedDTO user;

    public MembershipRestrictedDTO(MembershipDTO membership) {
        this.post = membership.getPost();
        this.group = membership.getGroup();
        this.unofficialPostName = membership.getUnofficialPostName();
        this.user = new UserRestrictedDTO(membership.getUser());
    }

    public PostDTO getPost() {
        return this.post;
    }

    public GroupDTO getGroup() {
        return this.group;
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
        MembershipRestrictedDTO that = (MembershipRestrictedDTO) o;
        return Objects.equals(this.post, that.post)
            && Objects.equals(this.group, that.group)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName)
            && Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post, this.group, this.unofficialPostName, this.user);
    }

    @Override
    public String toString() {
        return "RestrictedMembershipDTO{"
            + "post=" + this.post
            + ", fkitGroupDTO=" + this.group
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + ", user=" + this.user
            + '}';
    }
}
