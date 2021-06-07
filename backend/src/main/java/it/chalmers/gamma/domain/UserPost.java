package it.chalmers.gamma.domain;

public record UserPost(UserRestricted user,
                       Post post) { }
