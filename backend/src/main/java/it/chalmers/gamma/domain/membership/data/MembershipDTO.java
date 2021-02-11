package it.chalmers.gamma.domain.membership.data;

import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.UserDTO;
import java.util.Objects;

public class MembershipDTO {

    private final PostDTO post;
    private final GroupDTO group;
    private final String unofficialPostName;
    private final UserDTO user;

    private MembershipDTO(PostDTO post,
                         GroupDTO group,
                         String unofficialPostName,
                         UserDTO user) {
        this.post = post;
        this.group = group;
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

    public GroupDTO getGroup() {
        return this.group;
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
                && Objects.equals(this.group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.user,
                this.group);
    }

    @Override
    public String toString() {
        return "MembershipDTO{"
                + "post=" + this.post
                + ", group='" + this.group + '\''
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", user='" + this.user + '\''
                + '}';
    }



    public static class MembershipDTOBuilder {

        private PostDTO post;
        private GroupDTO group;
        private String unofficialPostName;
        private UserDTO user;

        public MembershipDTOBuilder post(PostDTO post) {
            this.post = post;
            return this;
        }

        public MembershipDTOBuilder group(GroupDTO group) {
            this.group = group;
            return this;
        }

        public MembershipDTOBuilder unofficialPostName(String unofficialPostName) {
            this.unofficialPostName = unofficialPostName;
            return this;
        }

        public MembershipDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public MembershipDTO build() {
            return new MembershipDTO(post, group, unofficialPostName, user);
        }
    }

}
