package it.chalmers.gamma.app.domain;

public record UserPost(UserRestricted user,
                       Post post) { }
