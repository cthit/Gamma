package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.util.entity.DTO;

public record Membership(Post post,
                         Group group,
                         String unofficialPostName,
                         UserRestricted user)
        implements DTO { }