package it.chalmers.gamma.domain.membership.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.group.service.GroupDTO;
import it.chalmers.gamma.domain.post.service.PostDTO;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.Objects;

public record MembershipRestrictedDTO(PostDTO post,
                                      GroupDTO group,
                                      String unofficialPostName,
                                      UserRestrictedDTO user)
        implements DTO {

    public MembershipRestrictedDTO(MembershipDTO m) {
        this(m.post(), m.group(), m.unofficialPostName(), new UserRestrictedDTO(m.user()));
    }

}

