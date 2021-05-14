package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.user.service.UserId;

public record MockMembership(UserId userId,
                             PostId postId,
                             String unofficialPostName) { }
