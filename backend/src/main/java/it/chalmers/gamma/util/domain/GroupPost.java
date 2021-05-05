package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.post.service.PostDTO;

public record GroupPost(PostDTO post, GroupDTO group) { }
