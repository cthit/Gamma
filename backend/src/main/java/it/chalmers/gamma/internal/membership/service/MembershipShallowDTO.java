package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.user.service.UserId;

public record MembershipShallowDTO(PostId postId,
                            GroupId groupId,
                            String unofficialPostName,
                            UserId userId) implements DTO { }
