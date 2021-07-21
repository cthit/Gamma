package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record Membership(Post post,
                         Group group,
                         String unofficialPostName,
                         User user) {

    //TODO: Add custom class for unofficialPostName

    public Membership {
        Objects.requireNonNull(post);
        Objects.requireNonNull(group);
        Objects.requireNonNull(unofficialPostName);
        Objects.requireNonNull(user);
    }

}