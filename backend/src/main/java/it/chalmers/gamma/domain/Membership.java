package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record Membership(Post post,
                         Group group,
                         String unofficialPostName,
                         UserRestricted user)
        implements DTO { }