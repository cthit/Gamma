package it.chalmers.gamma.domain.authority.controller.request;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

public class CreateAuthorityRequest {

    public PostId postId;
    public SuperGroupId superGroupId;
    public String authority;

}
