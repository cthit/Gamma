package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record Membership(Post post,
                         Group group,
                         String unofficialPostName,
                         UserRestricted user)
        implements DTO { }