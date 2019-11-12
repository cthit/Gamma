package it.chalmers.gamma.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.db.entity.Post;

import java.util.Objects;

public class MembershipDTO {
    private final Post post;
    private final String unofficialPostName;
    @JsonUnwrapped
    private final ITUserDTO user;

    public MembershipDTO(Post post,
                         String unofficialPostName,
                         ITUserDTO user) {
        this.post = post;
        this.unofficialPostName = unofficialPostName;
        this.user = user;
    }

    public Post getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public ITUserDTO getUser() {
        return user;
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
                && Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.user);
    }

    @Override
    public String toString() {
        return "MembershipDTO{"
                + "post=" + this.post
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", user='" + this.user + '\''
                + '}';
    }
}
