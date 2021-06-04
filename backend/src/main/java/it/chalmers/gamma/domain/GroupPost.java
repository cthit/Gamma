package it.chalmers.gamma.domain;

import it.chalmers.gamma.internal.post.service.PostDTO;

public record GroupPost(PostDTO post, Group group) { }
