package it.chalmers.gamma.domain.dto.membership;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.List;
import java.util.Objects;

public class MembershipDTO {
    private final PostDTO post;
    private final FKITGroupDTO fkitGroupDTO;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final ITUserDTO user;
    private final List<FKITSuperGroupDTO> fkitSuperGroupDTOS;


    public MembershipDTO(PostDTO post,
                         FKITGroupDTO fkitGroupDTO, String unofficialPostName,
                         ITUserDTO user, List<FKITSuperGroupDTO> fkitSuperGroupDTOS) {
        this.post = post;
        this.fkitGroupDTO = fkitGroupDTO;
        this.unofficialPostName = unofficialPostName;
        this.user = user;
        this.fkitSuperGroupDTOS = fkitSuperGroupDTOS;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public ITUserDTO getUser() {
        return user;
    }

    public List<FKITSuperGroupDTO> getFkitSuperGroupDTOS() {
        return fkitSuperGroupDTOS;
    }

    public FKITGroupDTO getFkitGroupDTO() {
        return fkitGroupDTO;
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
                && Objects.equals(this.fkitSuperGroupDTOS, that.fkitSuperGroupDTOS)
                && Objects.equals(this.fkitGroupDTO, that.fkitSuperGroupDTOS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.user,
                this.fkitSuperGroupDTOS,
                this.fkitGroupDTO);
    }

    @Override
    public String toString() {
        return "MembershipDTO{"
                + "post=" + this.post
                + ", fkitGroupDTO='" + this.fkitGroupDTO + '\''
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", user='" + this.user + '\''
                + ", fkitSuperGroupDTOS='" + this.fkitSuperGroupDTOS + '\''
                + '}';
    }
}
