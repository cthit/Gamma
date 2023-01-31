package it.chalmers.gamma.app.authoritylevel.domain;


import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.GammaUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RecordBuilder
public record AuthorityLevel(AuthorityLevelName name,
                             List<SuperGroupPost> posts,
                             List<SuperGroup> superGroups,
                             List<GammaUser> users) implements AuthorityLevelBuilder.With {

    public AuthorityLevel {
        Objects.requireNonNull(name);
        Objects.requireNonNull(posts);
        Objects.requireNonNull(superGroups);
        Objects.requireNonNull(users);
    }

    public static AuthorityLevel create(AuthorityLevelName name) {
        return new AuthorityLevel(name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public record SuperGroupPost(SuperGroup superGroup, Post post) {
    }
}