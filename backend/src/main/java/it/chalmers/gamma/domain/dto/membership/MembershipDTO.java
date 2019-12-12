package it.chalmers.gamma.domain.dto.membership;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.Objects;

public class MembershipDTO {
    private final PostDTO post;
    private final FKITGroupDTO fkitGroupDTO;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final ITUserDTO user;


    public MembershipDTO(PostDTO post,
                         FKITGroupDTO fkitGroupDTO, String unofficialPostName,
                         ITUserDTO user) {
        this.post = post;
        this.fkitGroupDTO = fkitGroupDTO;
        this.unofficialPostName = unofficialPostName;
        this.user = user;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public ITUserDTO getUser() {
        return this.user;
    }

    public FKITGroupDTO getFkitGroupDTO() {
        return this.fkitGroupDTO;
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
                && Objects.equals(this.fkitGroupDTO, that.fkitGroupDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.user,
                this.fkitGroupDTO);
    }

    @Override
    public String toString() {
        return "MembershipDTO{"
                + "post=" + this.post
                + ", fkitGroupDTO='" + this.fkitGroupDTO + '\''
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", user='" + this.user + '\''
                + '}';
    }
}
