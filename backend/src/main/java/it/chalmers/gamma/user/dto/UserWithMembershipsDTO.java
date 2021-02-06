package it.chalmers.gamma.user.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.post.PostDTO;

import java.util.List;

public class UserWithMembershipsDTO {

    @JsonUnwrapped
    private final UserDTO user;

    private final List<UserGroups> groups;

    public UserWithMembershipsDTO(UserDTO user, List<UserGroups> groups) {
        this.user = user;
        this.groups = groups;
    }

    public UserDTO getUser() {
        return user;
    }

    public List<UserGroups> getGroups() {
        return groups;
    }

    public static class UserGroups {
        private final PostDTO post;
        private final GroupDTO group;

        public UserGroups(PostDTO post, GroupDTO group) {
            this.post = post;
            this.group = group;
        }

        public PostDTO getPost() {
            return post;
        }

        public GroupDTO getGroup() {
            return group;
        }
    }
}
