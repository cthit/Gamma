package it.chalmers.gamma.app.domain;


import java.util.List;
import java.util.Objects;

public record Authorities(AuthorityLevelName authorityLevelName,
                            List<SuperGroupPost> posts,
                            List<SuperGroup> superGroups,
                            List<User> users) {

    public Authorities {
        Objects.requireNonNull(authorityLevelName);
        Objects.requireNonNull(posts);
        Objects.requireNonNull(superGroups);
        Objects.requireNonNull(users);
    }

    public record SuperGroupPost(SuperGroup superGroup, Post post) { }
}