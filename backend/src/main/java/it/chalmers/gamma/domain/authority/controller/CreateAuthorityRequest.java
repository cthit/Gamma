package it.chalmers.gamma.domain.authority.controller;

import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

public class CreateAuthorityRequest {

    protected CreateAuthorityRequest() { }

    protected PostId postId;
    protected SuperGroupId superGroupId;
    protected String authority;

}
