package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.post.service.PostDTO;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record MembershipDTO(PostDTO post,
                            GroupDTO group,
                            String unofficialPostName,
                            UserRestrictedDTO user)
        implements DTO { }