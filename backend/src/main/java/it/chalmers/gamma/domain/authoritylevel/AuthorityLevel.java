package it.chalmers.gamma.domain.authoritylevel;


import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RecordBuilder
public record AuthorityLevel(AuthorityLevelName name,
                             List<SuperGroupPost> posts,
                             List<SuperGroup> superGroups,
                             List<User> users) implements AuthorityLevelBuilder.With {

    public AuthorityLevel {
        Objects.requireNonNull(name);
        Objects.requireNonNull(posts);
        Objects.requireNonNull(superGroups);
        Objects.requireNonNull(users);
    }

    public static AuthorityLevel create(AuthorityLevelName name) {
        return new AuthorityLevel(name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public record SuperGroupPost(SuperGroup superGroup, Post post) { }
}