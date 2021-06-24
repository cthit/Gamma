package it.chalmers.gamma.app.membership.service;

import it.chalmers.gamma.util.entity.DTO;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.UserId;

public record MembershipShallowDTO(PostId postId,
                            GroupId groupId,
                            String unofficialPostName,
                            UserId userId) implements DTO { }
