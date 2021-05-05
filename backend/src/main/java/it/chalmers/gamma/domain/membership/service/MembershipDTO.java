package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.domain.group.service.GroupDTO;
import it.chalmers.gamma.domain.post.service.PostDTO;
import it.chalmers.gamma.domain.user.service.UserDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.Objects;

public record MembershipDTO(PostDTO post,
                            GroupDTO group,
                            String unofficialPostName,
                            UserDTO user)
        implements DTO { }