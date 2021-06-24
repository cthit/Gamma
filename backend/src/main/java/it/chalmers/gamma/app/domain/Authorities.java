package it.chalmers.gamma.app.domain;


import java.util.List;

public record Authorities(AuthorityLevelName authorityLevelName,
                            List<SuperGroupPost> posts,
                            List<SuperGroup> superGroups,
                            List<UserRestricted> users) {
    public record SuperGroupPost(SuperGroup superGroup, Post post) { }
}