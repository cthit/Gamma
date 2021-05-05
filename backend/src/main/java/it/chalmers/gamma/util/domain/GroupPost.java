package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.domain.group.service.GroupDTO;
import it.chalmers.gamma.domain.post.service.PostDTO;

public record GroupPost(PostDTO post, GroupDTO group) { }
