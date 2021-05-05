package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.user.service.UserId;

import java.util.Objects;

public record MembershipShallowDTO(PostId postId,
                            GroupId groupId,
                            String unofficialPostName,
                            UserId userId) implements DTO { }
