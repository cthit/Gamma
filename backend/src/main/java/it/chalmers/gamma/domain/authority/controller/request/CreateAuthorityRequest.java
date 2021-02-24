package it.chalmers.gamma.domain.authority.controller.request;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class CreateAuthorityRequest {

    public PostId postId;
    public SuperGroupId superGroupId;
    public String authority;

}
