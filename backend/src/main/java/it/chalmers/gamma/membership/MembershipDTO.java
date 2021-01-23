package it.chalmers.gamma.membership;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.group.GroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserDTO;
import java.util.Objects;

public class MembershipDTO {
    private final PostDTO post;
    private final GroupDTO groupDTO;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final UserDTO user;


    public MembershipDTO(PostDTO post,
                         GroupDTO groupDTO,
                         String unofficialPostName,
                         UserDTO user) {
        this.post = post;
        this.groupDTO = groupDTO;
        this.unofficialPostName = unofficialPostName;
        this.user = user;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public GroupDTO getFkitGroupDTO() {
        return this.groupDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MembershipDTO that = (MembershipDTO) o;
        return Objects.equals(this.post, that.post)
                && Objects.equals(this.unofficialPostName, that.unofficialPostName)
                && Objects.equals(this.user, that.user)
                && Objects.equals(this.groupDTO, that.groupDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.user,
                this.groupDTO);
    }

    @Override
    public String toString() {
        return "MembershipDTO{"
                + "post=" + this.post
                + ", fkitGroupDTO='" + this.groupDTO + '\''
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", user='" + this.user + '\''
                + '}';
    }
}
