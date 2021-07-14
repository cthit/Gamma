package it.chalmers.gamma.app.domain;


import java.util.List;
import java.util.Objects;

public record AuthorityLevel(AuthorityLevelName name,
                             List<SuperGroupPost> posts,
                             List<SuperGroup> superGroups,
                             List<User> users) {

    public AuthorityLevel {
        Objects.requireNonNull(name);
        Objects.requireNonNull(posts);
        Objects.requireNonNull(superGroups);
        Objects.requireNonNull(users);
    }

    public record SuperGroupPost(SuperGroup superGroup, Post post) { }
}