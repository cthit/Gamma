package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record Membership(Post post,
                         Group group,
                         String unofficialPostName,
                         User user)
        implements DTO {

    //TODO: Add custom class for unofficialPostName

    public Membership {
        Objects.requireNonNull(post);
        Objects.requireNonNull(group);
        Objects.requireNonNull(unofficialPostName);
        Objects.requireNonNull(user);
    }

}