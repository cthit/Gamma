package it.chalmers.gamma.domain;

import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;

public class GroupPost {

    public final PostDTO post;
    public final GroupDTO group;

    public GroupPost(PostDTO post, GroupDTO group) {
        this.post = post;
        this.group = group;
    }

}
