package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.util.entity.DTO;
import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.UserId;

public record MembershipShallowDTO(PostId postId,
                            GroupId groupId,
                            String unofficialPostName,
                            UserId userId) implements DTO { }
