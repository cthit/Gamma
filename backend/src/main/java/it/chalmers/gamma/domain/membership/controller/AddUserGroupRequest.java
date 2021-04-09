package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.user.service.UserId;

public class AddUserGroupRequest {

    protected UserId userId;
    protected PostId postId;
    protected String unofficialName;

    protected AddUserGroupRequest() { }
}
