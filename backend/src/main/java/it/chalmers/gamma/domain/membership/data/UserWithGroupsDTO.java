package it.chalmers.gamma.domain.membership.data;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;

import java.util.List;

public class UserWithGroupsDTO {

    @JsonUnwrapped
    private final UserDTO user;
    private final List<UserWithGroupsDTO.UserGroup> groups;

    public UserWithGroupsDTO(UserDTO user, List<UserWithGroupsDTO.UserGroup> groups) {
        this.user = user;
        this.groups = groups;
    }

    public UserDTO getUser() {
        return user;
    }

    public List<UserWithGroupsDTO.UserGroup> getGroups() {
        return groups;
    }

    public static class UserGroup {
        private final PostDTO post;
        private final GroupDTO group;

        public UserGroup(PostDTO post, GroupDTO group) {
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
